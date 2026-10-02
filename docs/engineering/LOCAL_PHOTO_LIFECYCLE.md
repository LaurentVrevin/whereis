# Local photo lifecycle — B3.2 / B4.1 / B4.2

## Product decision and boundaries

ADD_003 offers both `Choisir une photo` (Android Photo Picker, images only)
and `Prendre une photo` (external system camera / TakePicture). B3.2 resolves
the former open decision; the historical reference PDF remains unchanged.
There is no CameraX, custom gallery, general storage/CAMERA permission or cloud.
The external camera app accesses the hardware on Wheris's behalf.

`PhotoDraftReference` and permanent `PhotoReference` are distinct Android-free
opaque identifiers (`[A-Za-z0-9][A-Za-z0-9_-]*`). Only the permanent reference
is persisted. No URI, File, Context or physical path enters ViewModel/domain/
Room. `PhotoStorage` in domain exposes the lifecycle contract. `core:photo`
owns Android acquisition, private files and decoding, with actual consumers
in AddPin and repository coordination. It contains no Room, navigation or
Compose business UI. `feature:addpin` does not depend on `data`.

## Acquisition and preview

Picker contents are copied immediately into `cacheDir/photo_drafts/<id>`.
The owned copy no longer needs the external URI or persistable permission.
Camera targets are `cacheDir/photo_drafts/camera/<id>`. FileProvider authority
`${applicationId}.photo-drafts` is not exported and grants temporary access
only to this camera subdirectory. Picker drafts and permanent files are not
exposed. Both ActivityResult launchers stay in AddPinRoute.

Copying uses an application coroutine scope so rotation does not cancel it.
The neutral camera draft stays in the navigation-scoped ViewModel. Rotation
and foreground return do not automatically launch either system interaction.
Late acquisition results after real abandonment are discarded. Cancellation
cleans only the camera target, preserving all other fields and any prior photo.

A valid image must exist, be nonempty and be decodable. BitmapFactory bounds
and power-of-two sampling limit the largest decoded dimension to 1024 pixels.
Decoding and EXIF orientation run on Dispatchers.IO. The small AndroidX
ExifInterface 1.4.2 dependency supplies maintained orientation parsing on API 26;
no image loading library is added. The platform EXIF implementation has known
older-device security issues flagged by Android lint.
The Route preview reads only owned bytes (draft or promoted-pending-save).
Invalid content yields a readable error and can be retried/removed, preserving
text and favorite. The stateless form receives the preview as a UI slot.

## Canonical draft and save

CategorySelection owns accepted UserLocation (metadata/timestamp), selected
category and PlaceDetailsDraft (raw name/note, favorite, neutral photo).
Details and CategoryCreation wrap that same selection. Category Flow emissions
update it without erasing details. BackDetails/BackCategory preserve it; direct
Save after returning to ADD_002 persists every detail via the same CreatePinUseCase
and save guard as ADD_003. Optional fields stay optional; normalization stays
in CreatePinUseCase.

Promotion moves bytes to `filesDir/photos/<id>` and returns the same opaque ID.
An in-process lease protects promoted-pending-save files from reconciliation.
Retry reuses that file; preview resolves its new location. DB insertion failure
retains the entire draft, including photo. Only insertion success confirms Saved.
Submission is guarded from before promotion through insertion, and ownership
handoff completes without cancellation once persistence starts.

The repository also verifies that a permanent photo exists and is decodable
immediately before insertion, including retries using a cached promotion reference.
An absent or known corrupt file is rejected before any Room row is created.

Each Pin owns at most one photo; repository insertion rejects an already attached
reference. Explicit removal and real flow abandonment discard unattached drafts,
including promoted-pending-save files. Inner back navigation is not abandonment.
ViewModel clearing cleans through the application scope after any save finishes.
A failed cleanup remains discoverable in owned cache/pending storage for recovery;
it never crashes a late acquisition callback.

Failed on-screen removal retains its lease and pending bytes for preview, retry
or re-promotion. It invalidates the ViewModel's cached promotion so a later save
must revalidate and restore a permanent file before insertion. Real abandonment
releases the lease even when cleanup fails, allowing later reconciliation to
finish it. Pending files belonging to a live draft are preserved by reconciliation.

## Deletion and process recovery

PinRepositoryImpl serializes database/photo mutations using a mutex. Recovery
runs before first observation/save and on deletion. Filesystem operations run
off main. Room remains v4: no new schema, migration, table or BLOB.

Deletion stages `filesDir/photos/<id>` into `filesDir/photo_pending_delete/<id>`,
deletes the Room row, then removes the pending file. DB failure restores the
permanent photo; restoration failure preserves a durable pending file and propagates
the error. Final cleanup failure propagates with the row absent and pending file
recoverable. Deleting an absent Pin first reconciles pending cleanup and remains
idempotent. Room and filesystem are not a single transaction.

Deletion also checks for any other retained Pin reference before touching the
file. Unexpected legacy/shared references preserve the bytes until the last
row is removed; new shared references are rejected at insertion.

Reconciliation compares owned opaque file names with Room references:

- referenced pending file: restore to permanent, or remove redundant pending copy;
- unreferenced pending file: delete;
- referenced permanent file: keep;
- unreferenced permanent file: delete unless leased by an active in-process draft;
- abandoned cache draft from an old process: delete; current active draft: keep.

A fresh process has no old leases, so death after promotion and before insertion
cannot leave a permanent orphan indefinitely. Scans preserve every referenced photo
and never resolve arbitrary paths from user content. Errors remain retryable.

## Editing and backup boundary

Photos remain private and local. B4.1 introduces one canonical mutation:
`PinUpdate` / `UpdatePinUseCase` / `PinRepository.updatePin`. Its contract only
contains ID, category, name, note, favorite and explicit Keep/Remove/Replace.
The compatibility text editor delegates to it with Keep; no text-only SQL remains.
Position, accuracy, altitude and createdAt cannot be submitted. One transactional
UPDATE writes all editable columns and updatedAt, using the injected use-case clock.

Keep (including Replace with the current reference) does not access any file.
Absent Pin/category and already-attached replacement are rejected before file work.
Preflight and the Room transaction both check category/Pin existence; the transaction
also rechecks photo ownership and the previous photo reference. Replacement validates
the permanent owned bytes before staging the old photo. An unexpected shared old
reference is preserved for the other Pin rather than physically deleted.

Remove and Replace stage the old photo into pending deletion, then mutate Room.
DB failure restores the old photo; a replacement remains leased and unattached for
retry. Failed restoration leaves durable pending bytes for reconciliation. Once
staging begins, DB/restore/handoff/cleanup finish without caller cancellation;
cancellation still propagates to the caller, so it must not assume a cancelled call
means the DB did not commit. The replacement's lease is released only after commit.

`SUCCESS` and `SUCCESS_WITH_CLEANUP_PENDING` both mean the database mutation committed.
The latter must finish the editing workflow without offering a save retry as though
data were unsaved. It preserves pending cleanup and marks recovery necessary for
the next repository observation (or a subsequent deletion/fresh process). Errors
before commit return `TECHNICAL_FAILURE`; missing targets and shared replacement
have specific results. CancellationException is never converted into a normal result.

After process interruption, referenced pending old photos are restored if Room still
points to them; unreferenced pending old photos are cleaned if Room points to the new
photo. A live replacement lease protects a failed save's new bytes. A fresh process
has no such lease and removes an unreferenced new photo, preserving every DB reference.

## Editing draft and UI — B4.2

PLACE_002 now owns a full canonical draft (raw name/note, selected stable CategoryId,
favorite, dynamic categories, original PhotoReference and explicit photo intention).
Unchanged maps to Keep, Removed maps to Remove, Replacement(PhotoDraftReference)
promotes before mapping to Replace. A replacement records whether the original had
already been marked Removed; removing that replacement restores the previous intention.
Picking B then C abandons only B. Neither selecting nor removing a draft physically
touches the original photo. It is only retired by the canonical repository during save.

EditPinRoute owns PickVisualMedia(ImageOnly) and TakePicture, using the same Android
import/prepare/validate/FileProvider primitives as AddPin. An application coroutine
scope keeps copying/validation alive across rotation. Cancel cleans only a new camera
target, preserving every prior field/replacement. Late callbacks after abandoning the
editor clean their new draft. Back/Cancel and ViewModel clearing clean unattached new
drafts, including promoted ones after failed updates, after any running save finishes.
Saved drafts are never discarded. Save is guarded through promotion and DB mutation;
retry caches the draft-to-permanent reference without repeatedly promoting.

Permanent preview uses the Android-only `preview(PhotoReference)` overload, without
turning the persisted photo into a draft or acquiring a lease. Draft preview uses the
existing overload, including promoted-pending-save and pending bytes. Both use the
same decoder and the shared `core:ui` preview slot. No feature depends on another
feature. Preview failure changes only UI state and still permits removal/replacement;
it never silently clears a database reference.

Success and success-with-pending-cleanup return to PLACE_001 through the existing
navigation route. Missing Pin disables save; missing category allows reload/selection;
photo ownership and technical failures retain the whole draft for retry. Category
subscriptions update choices without overwriting edits, and category failures never
fabricate a category. The stateless screen resolves no files/repositories/launchers.

The navigation-scoped ViewModel preserves the whole draft during rotation/background.
SavedStateHandle restores simple name/note/category/favorite values after recreation;
photo intentions/replacement leases and pending system-camera results are not serialized.
Full photo editing UX restoration after process death is not guaranteed: the editor
reloads the persisted original and reconciliation cleans unreferenced old-process drafts.
The persisted Pin/photo remain protected by B4.1 recovery. A fresh photo must be selected
again if a replacement was not committed. No Bitmap, Uri, Context or File is serialized.

The B3.2 manual Picker/camera smoke is intentionally deferred to consolidation QA/B7;
it has not been executed and B4.2 does not claim otherwise.

`android:allowBackup="false"` disables cloud backup but does not guarantee exclusion
from every manufacturer device-to-device transfer. Before production release, Room
references and permanent photos require a joint backup/data-extraction decision
and device validation (SECURITY_PRIVACY sections 35–36). B3.2 does not change global
backup eligibility.

Platform contracts: [Photo Picker](https://developer.android.com/training/data-storage/shared/photo-picker),
[external camera](https://developer.android.com/media/camera/camera-deprecated/photobasics),
[FileProvider](https://developer.android.com/reference/androidx/core/content/FileProvider).

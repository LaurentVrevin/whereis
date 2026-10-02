# Local photo lifecycle — B3.2

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

Photos remain private and local. Existing text-only editing preserves favorite/photo;
full editing and replacing photos on saved Pins remain B4.

`android:allowBackup="false"` disables cloud backup but does not guarantee exclusion
from every manufacturer device-to-device transfer. Before production release, Room
references and permanent photos require a joint backup/data-extraction decision
and device validation (SECURITY_PRIVACY sections 35–36). B3.2 does not change global
backup eligibility.

Platform contracts: [Photo Picker](https://developer.android.com/training/data-storage/shared/photo-picker),
[external camera](https://developer.android.com/media/camera/camera-deprecated/photobasics),
[FileProvider](https://developer.android.com/reference/androidx/core/content/FileProvider).

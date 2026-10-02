# Local photo lifecycle — B3 contract

## Scope and ownership

B3.1 persists `PhotoReference` and `isFavorite`; it creates no image files.
Acquisition (camera, system selector or both) remains an open product decision.
No storage interface is introduced yet: the repository has no file storage implementation
or consumer, and draft handles/resolution belong to the future platform/storage boundary.

`PhotoReference.value` is an opaque identifier matching `[A-Za-z0-9][A-Za-z0-9_-]*`.
It contains no URI, path, file extension, user content or cloud identity. Unicode is supported
in place names/notes, but not in this storage identifier. The mapper validates persisted
identifiers and propagates an invalid reference rather than silently treating it as absent.
Identifier validation does not prove that a file exists.

The storage boundary must generate identifiers and associate them with permanent files
controlled by Wheris. A saved Pin owns its photo; do not share or delete a physical file
while another reference/workflow needs it. Category reassignment does not change ownership.

## Required B3.2 flow

1. Acquisition may create an application-controlled temporary draft.
2. A removal/cancellation deletes that draft and leaves the final reference null.
3. Acquisition failure preserves position, category, name, note and favorite; saving without
   a photo remains possible.
4. After user validation, storage promotes the draft to permanent internal storage and
   returns a `PhotoReference`. A persisted reference must never address a cache file.
5. The same `CreatePinUseCase` saves the enriched Pin. Promotion must complete before the
   database insertion. A storage failure must not persist a dangling reference.
6. If insertion fails, retain the promoted photo for a retry or clean it up on abandonment;
   preserve all other draft fields. Recovery must also handle a process interruption between
   promotion and insertion, so unattached permanent files are not silently orphaned.

The future storage target is a private permanent directory such as `Context.filesDir/photos`,
resolved only inside the Android storage implementation. No directory/files are created by
B3.1. Internal paths and Android URIs must not cross into core:model/domain or logs.

## Required deletion coordination before real photos are produced

The current path is `PinDetailViewModel.confirmDelete` → `PinRepository.deletePin` →
`PinRepositoryImpl.deletePin` → `PinDao.deletePin`. The data implementation is the
coordination point; a ViewModel must not manipulate files.

B3.2 must connect this path to owned-file cleanup before enabling real photo acquisition.
Capture the reference before deleting the row and make pending cleanup recoverable across
failure/process death (with durable retry or reconciliation). Delete a permanent file only
after checking that no retained Pin/workflow still needs it. A database failure must retain
the photo; a file failure after row deletion must remain recoverable and visible rather
than being swallowed. Retrying deletion of an already absent Pin must still recover pending
cleanup. Photo replacement/removal must follow the same ownership rules when B4 implements it.

Room transactions cover database operations only; they cannot make filesystem operations
atomic. B3.1 does not claim physical cleanup is implemented. This is a mandatory B3.2
integration requirement, including tests for insertion/deletion failure and process recovery.

## Editing and backup boundary

`updatePinDetails` edits name/note/time only and preserves favorite/photo. Full editing is B4.
`savePin` persists the complete Pin; direct and enriched creation use the same insertion.

The current manifest has `android:allowBackup="false"`, without explicit backup or
data-extraction XML rules. It disables cloud backup, but on some Android 12+ devices it
does not disable device-to-device transfer. A future `filesDir/photos` directory must not
be described as guaranteed excluded from every transfer under this configuration.
Before release, decide backup/restore eligibility for both Room references and permanent
photos together, then configure/test extraction rules. No global backup policy is changed
in B3.1. See `docs/product/SECURITY_PRIVACY.md`, sections 13 and 35–36, and
[Android Auto Backup documentation](https://developer.android.com/identity/data/autobackup).

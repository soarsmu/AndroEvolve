package de.tudarmstadt.informatik.tk.assistance.sdk.sensing.impl.contentobserver;

import android.Manifest.permission;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.BaseColumns;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.tudarmstadt.informatik.tk.assistance.sdk.db.DbContactEmailSensor;
import de.tudarmstadt.informatik.tk.assistance.sdk.db.DbContactNumberSensor;
import de.tudarmstadt.informatik.tk.assistance.sdk.db.DbContactSensor;
import de.tudarmstadt.informatik.tk.assistance.sdk.model.api.sensing.SensorApiType;
import de.tudarmstadt.informatik.tk.assistance.sdk.model.enums.EPushType;
import de.tudarmstadt.informatik.tk.assistance.sdk.provider.PreferenceProvider;
import de.tudarmstadt.informatik.tk.assistance.sdk.provider.dao.sensing.contact.ContactEmailSensorDao;
import de.tudarmstadt.informatik.tk.assistance.sdk.provider.dao.sensing.contact.ContactNumberSensorDao;
import de.tudarmstadt.informatik.tk.assistance.sdk.sensing.impl.AbstractContentObserverSensor;
import de.tudarmstadt.informatik.tk.assistance.sdk.util.DateUtils;
import de.tudarmstadt.informatik.tk.assistance.sdk.util.logger.Log;

/**
 * @author Unknown
 * @edited by Wladimir Schmidt (wlsc.dev@gmail.com)
 * @date 24.11.2015
 */
@Singleton
public final class ContactsSensor extends AbstractContentObserverSensor {

    private static final String TAG = ContactsSensor.class.getSimpleName();

    private static final Uri URI_EMAIL = Email.CONTENT_URI;
    private static final Uri URI_DATA = Data.CONTENT_URI;
    private static final Uri URI_PHONE = Phone.CONTENT_URI;
    private static final Uri URI_RAW_CONTACTS = RawContacts.CONTENT_URI;
    static final Uri URI_CONTACTS = Contacts.CONTENT_URI;

    private static final String[] columnsNote = {Note.NOTE};
    private static final String whereNote = Data.RAW_CONTACT_ID + " = ? AND " + Data.MIMETYPE + " = ?";

    private AsyncTask<Void, Void, Void> asyncTask;

    @Inject
    public ContactsSensor(Context context) {
        super(context);
    }

    @Override
    public int getType() {
        return SensorApiType.CONTACT;
    }

    @Override
    protected void syncData() {

        if (context == null || !isRunning()) {
            return;
        }

        if (ContextCompat.checkSelfPermission(
                context,
                permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "Permission was NOT granted!");
            setRunning(false);

            return;
        }

        long deviceId = PreferenceProvider.getInstance(context).getCurrentDeviceId();

        //Cursor cursor = context.getContentResolver().query(URI_RAW_CONTACTS, null, "deleted=?", new String[] { "0" }, null);

        ContentProviderClient contentClient = null,
                namesContentClient = null,
                numbersContentClient = null,
                emailsContentClient = null;

        Cursor contactsCursor = null;
        Cursor nameCur = null;

        final ContentResolver contentResolver = context.getContentResolver();
        contentClient = contentResolver.acquireContentProviderClient(URI_CONTACTS);

        if (contentClient == null) {
            Log.d(TAG, "contentClient is NULL");
            return;
        }

        namesContentClient = contentResolver.acquireContentProviderClient(URI_DATA);
        numbersContentClient = contentResolver.acquireContentProviderClient(URI_PHONE);
        emailsContentClient = contentResolver.acquireContentProviderClient(URI_EMAIL);

        try {

            contactsCursor = contentClient
                    .query(URI_CONTACTS, null, Data.IN_VISIBLE_GROUP + " = 1", null, null);

            if (contactsCursor == null || contactsCursor.getCount() <= 0) {
                Log.d(TAG, "cursor is NULL or empty");
                return;
            }

            final String[] projectionNameParams = {
                    StructuredName.GIVEN_NAME,
                    StructuredName.FAMILY_NAME};
            final String whereName = Data.MIMETYPE + " = ? AND " + Data.CONTACT_ID + " = ?";

            final ContentProviderClient finalNumbersContentClient = numbersContentClient;
            final ContentProviderClient finalEmailsContentClient = emailsContentClient;

            String created = DateUtils.dateToISO8601String(new Date(), Locale.getDefault());
            LongSparseArray<DbContactSensor> allExistingContacts = getAllExistingContacts();

            List<DbContactSensor> entriesToInsert = new ArrayList<>(contactsCursor.getCount());

//            long timeStart = System.nanoTime();

            while (contactsCursor.moveToNext() && isRunning()) {

                long contactId = getLongByColumnName(contactsCursor, BaseColumns._ID);
                String strContactId = String.valueOf(contactId);

                String strGivenName = null;
                String strFamilyName = null;

                String[] whereNameParams = {
                        StructuredName.CONTENT_ITEM_TYPE,
                        strContactId};

                if (namesContentClient != null) {

                    nameCur = namesContentClient.query(URI_DATA, projectionNameParams, whereName, whereNameParams, null);

                    if (nameCur != null && nameCur.moveToFirst()) {

                        strGivenName = getStringByColumnName(
                                nameCur,
                                StructuredName.GIVEN_NAME);
                        strFamilyName = getStringByColumnName(
                                nameCur,
                                StructuredName.FAMILY_NAME);
                    }
                }

                // Fill database object
                DbContactSensor sensorContact = new DbContactSensor();

                sensorContact.setContactId(contactId);
                sensorContact.setGlobalContactId(contactId);
                sensorContact.setDisplayName(getStringByColumnName(contactsCursor, Data.DISPLAY_NAME_PRIMARY));
                sensorContact.setGivenName(strGivenName);
                sensorContact.setFamilyName(strFamilyName);
                sensorContact.setStarred(getIntByColumnName(contactsCursor, Data.STARRED));
                sensorContact.setLastTimeContacted(getIntByColumnName(contactsCursor, Data.LAST_TIME_CONTACTED));
                sensorContact.setTimesContacted(getIntByColumnName(contactsCursor, Data.TIMES_CONTACTED));
                sensorContact.setNote(getNote(strContactId, namesContentClient));
                sensorContact.setIsNew(Boolean.TRUE);
                sensorContact.setIsDeleted(Boolean.FALSE);
                sensorContact.setIsUpdated(Boolean.TRUE);
                sensorContact.setCreated(created);
                sensorContact.setDeviceId(deviceId);

                if (checkForContactChange(allExistingContacts, sensorContact)) {
                    entriesToInsert.add(sensorContact);
                }
            }

//            long timeStop = System.nanoTime();
//            Log.d(TAG, "Execution time: " + TimeUnit.MILLISECONDS.convert(timeStop - timeStart, TimeUnit.NANOSECONDS));

            ContactNumberSensorDao contactNumberSensorDao = daoProvider.getContactNumberSensorDao();
            ContactEmailSensorDao contactEmailSensorDao = daoProvider.getContactEmailSensorDao();

            // insert sensor data in Tx
            if (!entriesToInsert.isEmpty()) {

                Log.d(TAG, "Inserting entries...");
                daoProvider.getContactSensorDao().insert(entriesToInsert);
                Log.d(TAG, "Finished");

                List<DbContactNumberSensor> numberEntriesToInsert = new ArrayList<>();
                List<DbContactEmailSensor> emailEntriesToInsert = new ArrayList<>();

                // get them again with IDs
                entriesToInsert.clear();
                entriesToInsert = daoProvider.getContactSensorDao().getAll(deviceId);

                for (DbContactSensor sensorContact : entriesToInsert) {

                    // get extra data
                    numberEntriesToInsert.addAll(getNumbers(sensorContact, finalNumbersContentClient));
                    emailEntriesToInsert.addAll(getMails(sensorContact, finalEmailsContentClient));
                }

                Log.d(TAG, "Contact number: Inserting entries");
                contactNumberSensorDao.insert(numberEntriesToInsert);
                Log.d(TAG, "Finished");

                Log.d(TAG, "Contact email: Inserting entries");
                contactEmailSensorDao.insert(emailEntriesToInsert);
                Log.d(TAG, "Finished");
            }

            List<DbContactNumberSensor> numberEntriesToInsert = new ArrayList<>();
            List<DbContactEmailSensor> emailEntriesToInsert = new ArrayList<>();

            // this deletes implicitly all numbers and mails which have no contact anymore
            for (int i = 0, size = allExistingContacts.size(); i < size; i++) {

                if (!isRunning()) {
                    return;
                }

                DbContactSensor contact = allExistingContacts.valueAt(i);

                numberEntriesToInsert.addAll(getNumbers(contact, finalNumbersContentClient));
                emailEntriesToInsert.addAll(getMails(contact, finalEmailsContentClient));
            }

            Log.d(TAG, "Contact number: Inserting entries");
            contactNumberSensorDao.insert(numberEntriesToInsert);
            Log.d(TAG, "Finished");

            Log.d(TAG, "Contact email: Inserting entries");
            contactEmailSensorDao.insert(emailEntriesToInsert);
            Log.d(TAG, "Finished");

            // remaining contacts are deleted
            deleteRemainingEntries(allExistingContacts, true);

        } catch (SecurityException se) {
            Log.d(TAG, "Permission was not granted for this event!");
        } catch (NumberFormatException e) {
            Log.d(TAG, "Number format exception", e);
        } catch (Exception e) {
            Log.e(TAG, "Some error in syncData", e);
        } finally {

            if (contactsCursor != null) {
                contactsCursor.close();
            }

            if (nameCur != null) {
                nameCur.close();
            }

            if (contentClient != null) {
                if (VERSION.SDK_INT < VERSION_CODES.N) {
                    contentClient.release();
                } else {
                    contentClient.close();
                }
            }

            if (namesContentClient != null) {
                if (VERSION.SDK_INT < VERSION_CODES.N) {
                    namesContentClient.release();
                } else {
                    namesContentClient.close();
                }
            }

            if (numbersContentClient != null) {
                if (VERSION.SDK_INT < VERSION_CODES.N) {
                    numbersContentClient.release();
                } else {
                    numbersContentClient.close();
                }
            }

            if (emailsContentClient != null) {
                if (VERSION.SDK_INT < VERSION_CODES.N) {
                    emailsContentClient.release();
                } else {
                    emailsContentClient.close();
                }
            }
        }
    }

    private boolean deleteRemainingEntries(LongSparseArray<DbContactSensor> allExistingContacts, boolean b) {

        boolean bSomethingDeleted = false;

        List<DbContactSensor> entriesToDelete = new ArrayList<>(allExistingContacts.size());

        for (int i = 0, size = allExistingContacts.size(); i < size; i++) {

            if (b && !isRunning()) {
                return bSomethingDeleted;
            }

            DbContactSensor dbContact = allExistingContacts.valueAt(i);

            dbContact.setIsDeleted(Boolean.TRUE);
            dbContact.setIsNew(Boolean.FALSE);
            dbContact.setIsUpdated(Boolean.TRUE);

            entriesToDelete.add(dbContact);

            if (!bSomethingDeleted) {
                bSomethingDeleted = true;
            }
        }

        // remove entries
        daoProvider.getContactSensorDao().delete(entriesToDelete);

        return bSomethingDeleted;
    }

    private boolean deleteRemainingEmailEntries(Map<String, DbContactEmailSensor> allExistingContactsEmail, boolean b) {

        boolean bSomethingDeleted = false;

        List<DbContactEmailSensor> entriesToDelete = new ArrayList<>(allExistingContactsEmail.size());

        for (Entry<String, DbContactEmailSensor> entry : allExistingContactsEmail.entrySet()) {

            if (b && !isRunning()) {
                return bSomethingDeleted;
            }

            DbContactEmailSensor dbContactEmail = entry.getValue();

            dbContactEmail.setIsDeleted(Boolean.TRUE);
            dbContactEmail.setIsNew(Boolean.FALSE);
            dbContactEmail.setIsUpdated(Boolean.TRUE);

            entriesToDelete.add(dbContactEmail);

            if (!bSomethingDeleted) {
                bSomethingDeleted = true;
            }
        }

        // delete entries
        daoProvider.getContactEmailSensorDao().delete(entriesToDelete);

        return bSomethingDeleted;
    }

    private boolean deleteRemainingNumberEntries(Map<String, DbContactNumberSensor> allExistingContactsNumber, boolean b) {

        boolean bSomethingDeleted = false;

        List<DbContactNumberSensor> entriesToDelete = new ArrayList<>(allExistingContactsNumber.size());

        for (Entry<String, DbContactNumberSensor> entry : allExistingContactsNumber.entrySet()) {

            if (b && !isRunning()) {
                return bSomethingDeleted;
            }

            DbContactNumberSensor dbContactNumber = entry.getValue();

            dbContactNumber.setIsDeleted(Boolean.TRUE);
            dbContactNumber.setIsNew(Boolean.FALSE);
            dbContactNumber.setIsUpdated(Boolean.TRUE);

            entriesToDelete.add(dbContactNumber);

            if (!bSomethingDeleted) {
                bSomethingDeleted = true;
            }
        }

        // delete entries
        daoProvider.getContactNumberSensorDao().delete(entriesToDelete);

        return bSomethingDeleted;
    }

    private List<DbContactEmailSensor> getMails(DbContactSensor sensorContact,
                                                ContentProviderClient finalEmailsContentClient) {

        if (finalEmailsContentClient == null) {
            Log.d(TAG, "finalEmailsContentClient is NULL");
            return Collections.emptyList();
        }

        long deviceId = PreferenceProvider.getInstance(context).getCurrentDeviceId();
        long longContactId = sensorContact.getContactId();
        Map<String, DbContactEmailSensor> mapExistingMails = getExistingMails(longContactId);

        String[] columns = {
                Email.ADDRESS,
                Email.TYPE
        };

        Cursor emailsCursor = null;

        try {

            emailsCursor = finalEmailsContentClient
                    .query(URI_EMAIL,
                            columns,
                            Email.CONTACT_ID + " = " + longContactId,
                            null,
                            null);

            if (emailsCursor == null || emailsCursor.getCount() <= 0) {
                return Collections.emptyList();
            }

            String created = DateUtils.dateToISO8601String(new Date(), Locale.getDefault());

            List<DbContactEmailSensor> entriesToInsert = new ArrayList<>(emailsCursor.getCount());

            while (emailsCursor.moveToNext()) {

                DbContactEmailSensor sensorContactMail = new DbContactEmailSensor();

                sensorContactMail.setMailId(getLongByColumnName(emailsCursor, Email._ID));
                sensorContactMail.setContactId(sensorContact.getId());
                sensorContactMail.setAddress(getStringByColumnName(emailsCursor, Email.ADDRESS));
                sensorContactMail.setType(getStringByColumnName(emailsCursor, Email.TYPE));
                sensorContactMail.setIsNew(Boolean.TRUE);
                sensorContactMail.setIsDeleted(Boolean.FALSE);
                sensorContactMail.setIsUpdated(Boolean.TRUE);
                sensorContactMail.setCreated(created);
                sensorContactMail.setDeviceId(deviceId);

                if (checkForContactMailChange(mapExistingMails, sensorContactMail)) {
                    entriesToInsert.add(sensorContactMail);
                }
            }

            // remaining mails are deleted
            deleteRemainingEmailEntries(mapExistingMails, false);

            return entriesToInsert;

        } catch (Exception e) {
            Log.e(TAG, "Some error in getMails");
        } finally {
            if (emailsCursor != null) {
                emailsCursor.close();
            }
        }

        return Collections.emptyList();
    }

    private boolean checkForContactMailChange(
            Map<String, DbContactEmailSensor> map,
            DbContactEmailSensor newItem) {

        String id = newItem.getAddress();

        DbContactEmailSensor existingItem = map.remove(id);

        if (existingItem == null) {

            newItem.setIsNew(Boolean.TRUE);
            newItem.setIsUpdated(Boolean.TRUE);
            newItem.setIsDeleted(Boolean.FALSE);

            return true;

        }
        if (hasContactMailDifference(existingItem, newItem)) {

            newItem.setIsNew(Boolean.FALSE);
            newItem.setIsUpdated(Boolean.TRUE);
            newItem.setIsDeleted(Boolean.FALSE);
            newItem.setId(existingItem.getId());

            return true;
        }

        return false;
    }

    private boolean hasContactMailDifference(
            DbContactEmailSensor existingMail,
            DbContactEmailSensor newMail) {

        return checkForDifference(existingMail.getAddress(), newMail.getAddress()) ||
                checkForDifference(existingMail.getType(), newMail.getType());
    }

    private List<DbContactNumberSensor> getNumbers(DbContactSensor sensorContact,
                                                   ContentProviderClient finalPhoneNumbersContentClient) {

        if (finalPhoneNumbersContentClient == null) {
            Log.d(TAG, "NumbersContentClient is NULL");
            return Collections.emptyList();
        }

        long deviceId = PreferenceProvider.getInstance(context).getCurrentDeviceId();
        long longContactId = sensorContact.getGlobalContactId();
        Map<String, DbContactNumberSensor> mapExistingNumbers = getExistingNumbers(longContactId);

        Cursor curPhones = null;

        try {

            curPhones = finalPhoneNumbersContentClient
                    .query(URI_PHONE,
                            null,
                            Phone.CONTACT_ID + " = " + longContactId,
                            null,
                            null);

            if (curPhones == null || curPhones.getCount() <= 0) {
                return Collections.emptyList();
            }

            String created = DateUtils.dateToISO8601String(new Date(), Locale.getDefault());

            List<DbContactNumberSensor> entriesToInsert = new ArrayList<>(curPhones.getCount());

            while (curPhones.moveToNext()) {

                DbContactNumberSensor sensorContactNumber = new DbContactNumberSensor();

                sensorContactNumber.setNumberId(getLongByColumnName(curPhones, BaseColumns._ID));
                sensorContactNumber.setContactId(sensorContact.getId());
                sensorContactNumber.setNumber(getStringByColumnName(curPhones, Phone.NUMBER));
                sensorContactNumber.setType(getStringByColumnName(curPhones, Phone.TYPE));
                sensorContactNumber.setIsNew(Boolean.TRUE);
                sensorContactNumber.setIsDeleted(Boolean.FALSE);
                sensorContactNumber.setIsUpdated(Boolean.TRUE);
                sensorContactNumber.setCreated(created);
                sensorContactNumber.setDeviceId(deviceId);

                if (checkForContactNumberChange(mapExistingNumbers, sensorContactNumber)) {
                    entriesToInsert.add(sensorContactNumber);
                }
            }

            // remaining numbers are deleted
            deleteRemainingNumberEntries(mapExistingNumbers, false);

            return entriesToInsert;

        } catch (Exception e) {
            Log.e(TAG, "Some error in getNumbers", e);
        } finally {
            if (curPhones != null) {
                curPhones.close();
            }
        }

        return Collections.emptyList();
    }

    private boolean checkForContactNumberChange(
            Map<String, DbContactNumberSensor> map,
            DbContactNumberSensor newItem) {

        String id = newItem.getNumber();

        DbContactNumberSensor existingItem = map.remove(id);

        if (existingItem == null) {

            newItem.setIsNew(Boolean.TRUE);
            newItem.setIsUpdated(Boolean.TRUE);
            newItem.setIsDeleted(Boolean.FALSE);

            return true;

        }
        if (hasContactNumberDifference(existingItem, newItem)) {

            newItem.setIsNew(Boolean.FALSE);
            newItem.setIsUpdated(Boolean.TRUE);
            newItem.setIsDeleted(Boolean.FALSE);
            newItem.setId(existingItem.getId());

            return true;
        }

        return false;
    }

    private boolean hasContactNumberDifference(DbContactNumberSensor existingNumber,
                                               DbContactNumberSensor newSensorContactNumber) {

        return checkForDifference(existingNumber.getNumber(), newSensorContactNumber.getNumber()) ||
                checkForDifference(existingNumber.getType(), newSensorContactNumber.getType());

    }

    private boolean checkForContactChange(LongSparseArray<DbContactSensor> map, DbContactSensor newItem) {

        long id = newItem.getContactId();
        DbContactSensor existingReminder = map.get(id);

        boolean result = false;

        DbContactSensor existingItem = map.get(id);
        map.delete(id);

        if (existingItem == null) {

            newItem.setIsNew(Boolean.TRUE);
            newItem.setIsUpdated(Boolean.TRUE);
            newItem.setIsDeleted(Boolean.FALSE);

            result = true;

        } else {
            if (hasContactDifference(existingItem, newItem)) {

                newItem.setIsNew(Boolean.FALSE);
                newItem.setIsUpdated(Boolean.TRUE);
                newItem.setIsDeleted(Boolean.FALSE);
                newItem.setId(existingItem.getId());

                result = true;
            }
        }

        if (!result) {
            newItem.setId(existingReminder.getId());
        }

        return result;
    }

    private boolean hasContactDifference(DbContactSensor existingReminder, DbContactSensor newSensorContact) {

        if (checkForDifference(existingReminder.getDisplayName(), newSensorContact.getDisplayName())) {
            return true;
        }
        if (checkForDifference(existingReminder.getGivenName(), newSensorContact.getGivenName())) {
            return true;
        }
        if (checkForDifference(existingReminder.getFamilyName(), newSensorContact.getFamilyName())) {
            return true;
        }
        if (checkForDifference(existingReminder.getStarred(), newSensorContact.getStarred())) {
            return true;
        }
        if (checkForDifference(existingReminder.getLastTimeContacted(), newSensorContact.getLastTimeContacted())) {
            return true;
        }
        if (checkForDifference(existingReminder.getTimesContacted(), newSensorContact.getTimesContacted())) {
            return true;
        }
        if (checkForDifference(existingReminder.getNote(), newSensorContact.getNote())) {
            return true;
        }
        return false;
    }

    private LongSparseArray<DbContactSensor> getAllExistingContacts() {

        long deviceId = PreferenceProvider.getInstance(context).getCurrentDeviceId();

        List<DbContactSensor> allContacts = daoProvider.getContactSensorDao().getAll(deviceId);
        LongSparseArray<DbContactSensor> result = new LongSparseArray<>(allContacts.size());

        for (DbContactSensor event : allContacts) {
            result.put(event.getGlobalContactId(), event);
        }

        return result;
    }

    private Map<String, DbContactNumberSensor> getExistingNumbers(long contactId) {

        List<DbContactNumberSensor> list = daoProvider
                .getContactNumberSensorDao()
                .getAll(contactId);

        Map<String, DbContactNumberSensor> map = new HashMap<>(list.size());

        for (DbContactNumberSensor number : list) {
            map.put(number.getNumber(), number);
        }

        return map;
    }

    private Map<String, DbContactEmailSensor> getExistingMails(long contactId) {

        List<DbContactEmailSensor> contactMails = daoProvider
                .getContactEmailSensorDao()
                .getAll(contactId);

        Map<String, DbContactEmailSensor> map = new HashMap<>(contactMails.size());

        for (DbContactEmailSensor mail : contactMails) {
            map.put(mail.getAddress(), mail);
        }

        return map;
    }

    private String getNote(String contactId, ContentProviderClient namesContentClient) {

        if (contactId == null || namesContentClient == null || context == null) {
            return null;
        }

        String note = "";
        String[] whereParameters = {contactId, Note.CONTENT_ITEM_TYPE};

        Cursor contacts = null;

        try {

            contacts = namesContentClient
                    .query(URI_DATA, columnsNote, whereNote, whereParameters, null);

            if (contacts != null && contacts.moveToFirst()) {
                note = getStringByColumnName(contacts, Note.NOTE);
            }

        } catch (Exception e) {
            Log.d(TAG, "Some error in getNote");
        } finally {
            if (contacts != null) {
                contacts.close();
            }
        }

        return note;
    }

    @Override
    public void startSensor() {

        asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                syncData();
                context.getContentResolver().registerContentObserver(URI_CONTACTS, true, mObserver);

                return null;
            }

        };
        asyncTask.execute();

        setRunning(true);
    }

    @Override
    public void stopSensor() {

        if (asyncTask != null && !asyncTask.isCancelled()) {
            asyncTask.cancel(true);
        }

        asyncTask = null;

        setRunning(false);
    }

    @Override
    public EPushType getPushType() {
        return EPushType.MANUALLY_WLAN_ONLY;
    }

    @Override
    public void dumpData() {

    }

    @Override
    public void updateSensorInterval(Double collectionInterval) {

    }

    @Override
    public void reset() {

    }
}
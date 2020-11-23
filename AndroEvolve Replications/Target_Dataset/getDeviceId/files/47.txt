package com.cashloan.jumidai.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Leehor
 * date   : 2019/7/1520:51
 * version: 1.0
 * desc   : 多卡槽时获取IMEI
 */
@SuppressLint("MissingPermission")
public class getImei {

        Context context = null;

        public getImei(Context _context) {
            context = _context;
        }

        public String getimei() {

            try {

                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                String imei = tm.getDeviceId();
                List<String> IMEIS = new ArrayList<String>();
                if (checkimei(imei.trim())) {
                    IMEIS.add(imei.trim());
                }


                try{
                    TelephonyManager telephonyManager1 = (TelephonyManager)context.getSystemService("phone1");
                    String imeiphone1=   telephonyManager1.getDeviceId();
                    if (imeiphone1 != null && checkimei(imeiphone1)) {
                        if (!IMEIS.contains(imeiphone1)) {
                            IMEIS.add(imeiphone1);
                        }
                    }
                }  catch (Exception e) {

                }
                try{
                    TelephonyManager telephonyManager2 = (TelephonyManager)context.getSystemService("phone2");
                    String imeiphone2=   telephonyManager2.getDeviceId();
                    if (imeiphone2 != null && checkimei(imeiphone2)) {
                        if (!IMEIS.contains(imeiphone2)) {
                            IMEIS.add(imeiphone2);
                        }
                    }
                }  catch (Exception e) {

                }

                List<String> imeis = IMEI_initQualcommDoubleSim();
                if (imeis != null && imeis.size() > 0) {
                    for (String item : imeis) {
                        if (!IMEIS.contains(item)) {
                            IMEIS.add(item);
                        }
                    }
                }

                imeis = IMEI_initMtkSecondDoubleSim();
                if (imeis != null && imeis.size() > 0) {
                    for (String item : imeis) {
                        if (!IMEIS.contains(item)) {
                            IMEIS.add(item);
                        }
                    }
                }
                imeis = IMEI_initMtkDoubleSim();
                if (imeis != null && imeis.size() > 0) {
                    for (String item : imeis) {
                        if (!IMEIS.contains(item)) {
                            IMEIS.add(item);
                        }
                    }
                }
                imeis = IMEI_initSpreadDoubleSim();
                if (imeis != null && imeis.size() > 0) {
                    for (String item : imeis) {
                        if (!IMEIS.contains(item)) {
                            IMEIS.add(item);
                        }
                    }
                }

                StringBuffer IMEI_SB = new StringBuffer();

                Integer TIMES_TEMP = 1;
                for (String item : IMEIS) {
                    if (TIMES_TEMP > 1) {
                        IMEI_SB.append('\n');
                    }
                    IMEI_SB.append(item);
                    // params.put("IMEI" + TIMES_TEMP, item);
                    TIMES_TEMP++;
                }

                String imeis_tmp = IMEI_SB.toString().trim();


                if ("".equals(imeis_tmp)) {
                    imeis_tmp = "no_imei_1";
                }
                return imeis_tmp;


            } catch (Exception e) {
                return "no_imei_2";
            }

        }

        private Boolean checkimeisame(String imei) {
            char firstchar = '0';
            if (imei.length() > 0) {
                firstchar = imei.charAt(0);
            }
            Boolean issame = true;
            for (int i = 0; i < imei.length(); i++) {
                char ch = imei.charAt(i);
                if (firstchar != ch) {
                    issame = false;
                    break;
                }
            }
            return issame;


        }

        private Boolean checkimei(String IMEI) {
            Integer LEN = IMEI.length();
            if (LEN > 10 && LEN < 20 && !checkimeisame(IMEI.trim())) {
                return true;
            }
            return false;
        }

        private List<String> IMEI_initMtkDoubleSim() {
            try {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                Class<?> c = Class.forName("com.android.internal.telephony.Phone");
                Integer simId_1, simId_2;
                try {
                    java.lang.reflect.Field fields1 = c.getField("GEMINI_SIM_1");
                    fields1.setAccessible(true);
                    simId_1 = (Integer) fields1.get(null);
                    java.lang.reflect.Field fields2 = c.getField("GEMINI_SIM_2");
                    fields2.setAccessible(true);
                    simId_2 = (Integer) fields2.get(null);
                } catch (Exception ex) {
                    simId_1 = 0;
                    simId_2 = 1;
                }


                java.lang.reflect.Method m1 = TelephonyManager.class
                        .getDeclaredMethod("getDeviceIdGemini", int.class);
                String imei_1 = ((String) m1.invoke(tm, simId_1)).trim();
                String imei_2 = ((String) m1.invoke(tm, simId_2)).trim();



                List<String> imeis = new ArrayList<String>();
                if (checkimei(imei_1)) {
                    imeis.add(imei_1);
                }
                if (checkimei(imei_2)) {
                    imeis.add(imei_2);
                }
                return imeis;
            } catch (Exception e) {
                // 返回null
                return null;
            }

        }

        private List<String> IMEI_initMtkSecondDoubleSim() {
            try {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                Class<?> c = Class.forName("com.android.internal.telephony.Phone");

                Integer simId_1, simId_2;
                try {
                    java.lang.reflect.Field fields1 = c.getField("GEMINI_SIM_1");
                    fields1.setAccessible(true);
                    simId_1 = (Integer) fields1.get(null);
                    java.lang.reflect.Field fields2 = c.getField("GEMINI_SIM_2");
                    fields2.setAccessible(true);
                    simId_2 = (Integer) fields2.get(null);
                } catch (Exception ex) {
                    simId_1 = 0;
                    simId_2 = 1;
                }

                java.lang.reflect.Method mx = TelephonyManager.class.getMethod(
                        "getDefault", int.class);
                TelephonyManager tm1 = (TelephonyManager) mx.invoke(tm, simId_1);
                TelephonyManager tm2 = (TelephonyManager) mx.invoke(tm, simId_2);

                String imei_1 = (tm1.getDeviceId()).trim();
                String imei_2 = (tm2.getDeviceId()).trim();

                List<String> imeis = new ArrayList<String>();
                if (checkimei(imei_1)) {
                    imeis.add(imei_1);
                }
                if (checkimei(imei_2)) {
                    imeis.add(imei_2);
                }
                return imeis;

            } catch (Exception e) {
                return null;
            }
        }

        private List<String> IMEI_initSpreadDoubleSim() {
            try {
                Class<?> c = Class
                        .forName("com.android.internal.telephony.PhoneFactory");
                java.lang.reflect.Method m = c.getMethod("getServiceName",
                        String.class, int.class);
                String spreadTmService = (String) m.invoke(c,
                        Context.TELEPHONY_SERVICE, 1);

                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                // String imsi_1 = tm.getSubscriberId();
                String imei_1 = (tm.getDeviceId()).trim();
                // Integer phoneType_1 = tm.getPhoneType();
                TelephonyManager tm1 = (TelephonyManager) context
                        .getSystemService(spreadTmService);
                // String imsi_2 = tm1.getSubscriberId();
                String imei_2 = (tm1.getDeviceId()).trim();


                List<String> imeis = new ArrayList<String>();
                if (checkimei(imei_1)) {
                    imeis.add(imei_1);
                }
                if (checkimei(imei_2)) {
                    imeis.add(imei_2);
                }
                return imeis;

            } catch (Exception e) {
                return null;
            }
        }

        public List<String> IMEI_initQualcommDoubleSim() {
            try {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                Class<?> cx = Class
                        .forName("android.telephony.MSimTelephonyManager");
                Object obj = context.getSystemService("phone_msim");
                Integer simId_1 = 0;
                Integer simId_2 = 1;
                java.lang.reflect.Method md = cx
                        .getMethod("getDeviceId", int.class);

                String imei_1 = ((String) md.invoke(obj, simId_1)).trim();
                String imei_2 = ((String) md.invoke(obj, simId_2)).trim();

                List<String> imeis = new ArrayList<String>();
                if (checkimei(imei_1)) {
                    imeis.add(imei_1);
                }
                if (checkimei(imei_2)) {
                    imeis.add(imei_2);
                }
                return imeis;

            } catch (Exception e) {
                return null;
            }
        }

    }

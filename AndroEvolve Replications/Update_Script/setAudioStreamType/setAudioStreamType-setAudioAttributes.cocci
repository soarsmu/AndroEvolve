@upperbottom_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
classIden.setAudioAttributes(iden0);
+ } else {
+ int newParameterVariable0 = AudioManager.STREAM_VOICE_CALL;
+ classIden.setAudioStreamType(newParameterVariable0);
+ }

@bottomupper_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
+ AudioAttributes newParameterVariable0 = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING).build();
+ classIden.setAudioAttributes(newParameterVariable0);
+ } else {
classIden.setAudioStreamType(iden0);
+ }

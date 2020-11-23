@upperbottom_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
 classIden.requestAudioFocus(iden0);
+ } else {
+ OnAudioFocusChangeListener newParameterVariable0 = this;
+ int newParameterVariable1 = AudioManager.STREAM_MUSIC;
+ int newParameterVariable2 = AudioManager.AUDIOFOCUS_GAIN;
+ classIden.requestAudioFocus(newParameterVariable0,newParameterVariable1,newParameterVariable2);
+ }

@bottomupper_classname@
expression exp0, exp1, exp2, exp3;
identifier iden0, iden1, iden2, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
+ AudioFocusRequest request = new AudioFocusRequestOreo(this).getAudioFocusRequest();
+ AudioFocusRequest newParameterVariable0 = request;
+ classIden.requestAudioFocus(newParameterVariable0);
+ } else {
 classIden.requestAudioFocus(iden0, iden1, iden2);
+ }


@upperbottom_classname_assignment@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
tempFunctionReturnValue = classIden.requestAudioFocus(iden0);
+ } else {
+ OnAudioFocusChangeListener newParameterVariable0 = this;
+ int newParameterVariable1 = AudioManager.STREAM_MUSIC;
+ int newParameterVariable2 = AudioManager.AUDIOFOCUS_GAIN;
+ tempFunctionReturnValue = classIden.requestAudioFocus(newParameterVariable0,newParameterVariable1,newParameterVariable2);
+ }

@bottomupper_classname_assignment@
expression exp0, exp1, exp2, exp3;
identifier iden0, iden1, iden2, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
+ AudioFocusRequest request = new AudioFocusRequestOreo(this).getAudioFocusRequest();
+ AudioFocusRequest newParameterVariable0 = request;
+ tempFunctionReturnValue = classIden.requestAudioFocus(newParameterVariable0);
+ } else {
tempFunctionReturnValue = classIden.requestAudioFocus(iden0, iden1, iden2);
+ }

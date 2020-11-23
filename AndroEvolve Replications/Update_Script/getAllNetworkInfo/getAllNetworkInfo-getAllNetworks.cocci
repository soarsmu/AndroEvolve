@upperbottom_classname@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
 classIden.getAllNetworks();
+ } else {
+ NetworkInfo[] info;
+ NetworkInfo[] tempFunctionReturnValue;
+ ConnectivityManager newClassName = connectivityManager;
+ newClassName.getAllNetworkInfo();
+ }

@bottomupper_classname@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
+ ConnectivityManager newClassName = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
+ newClassName.getAllNetworks();
+ } else {
 classIden.getAllNetworkInfo();
+ }


@upperbottom_classname_assignment@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
tempFunctionReturnValue = classIden.getAllNetworks();
+ } else {
+ NetworkInfo[] info;
+ NetworkInfo[] tempFunctionReturnValue;
+ ConnectivityManager newClassName = connectivityManager;
+ tempFunctionReturnValue = newClassName.getAllNetworkInfo();
+ }

@bottomupper_classname_assignment@
expression exp0;
identifier classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
+ ConnectivityManager newClassName = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
+ tempFunctionReturnValue = newClassName.getAllNetworks();
+ } else {
tempFunctionReturnValue = classIden.getAllNetworkInfo();
+ }

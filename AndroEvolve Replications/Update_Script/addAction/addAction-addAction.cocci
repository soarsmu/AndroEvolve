@upperbottom_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
 classIden.addAction(iden0);
+ } else {
+ int newParameterVariable0 = icon;
+ CharSequence newParameterVariable1 = title;
+ PendingIntent newParameterVariable2 = intent;
+ classIden.addAction(newParameterVariable0,newParameterVariable1,newParameterVariable2);
+ }

@bottomupper_classname@
expression exp0, exp1, exp2, exp3;
identifier iden0, iden1, iden2, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
+ Notification.Action action = new Notification.Action.Builder(Icon.createWithResource(context, icon), title, intent).build();
+ Action newParameterVariable0 = new Notification.Action.Builder(Icon.createWithResource(context, icon), title, intent).build();
+ classIden.addAction(newParameterVariable0);
+ } else {
 classIden.addAction(iden0, iden1, iden2);
+ }


@upperbottom_classname_assignment@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
tempFunctionReturnValue = classIden.addAction(iden0);
+ } else {
+ int newParameterVariable0 = icon;
+ CharSequence newParameterVariable1 = title;
+ PendingIntent newParameterVariable2 = intent;
+ tempFunctionReturnValue = classIden.addAction(newParameterVariable0,newParameterVariable1,newParameterVariable2);
+ }

@bottomupper_classname_assignment@
expression exp0, exp1, exp2, exp3;
identifier iden0, iden1, iden2, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
+ Notification.Action action = new Notification.Action.Builder(Icon.createWithResource(context, icon), title, intent).build();
+ Action newParameterVariable0 = new Notification.Action.Builder(Icon.createWithResource(context, icon), title, intent).build();
+ tempFunctionReturnValue = classIden.addAction(newParameterVariable0);
+ } else {
tempFunctionReturnValue = classIden.addAction(iden0, iden1, iden2);
+ }

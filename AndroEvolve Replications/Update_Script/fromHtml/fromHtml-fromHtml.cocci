@upperbottom_classname@
expression exp0, exp1, exp2;
identifier iden0, iden1, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
 classIden.fromHtml(iden0, iden1);
+ } else {
+ classIden.fromHtml(iden0);
+ }

@bottomupper_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
+ int newParameterVariable1 = 0;
+ classIden.fromHtml(iden0,newParameterVariable1);
+ } else {
 classIden.fromHtml(iden0);
+ }


@upperbottom_classname_assignment@
expression exp0, exp1, exp2;
identifier iden0, iden1, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
tempFunctionReturnValue = classIden.fromHtml(iden0, iden1);
+ } else {
+ tempFunctionReturnValue = classIden.fromHtml(iden0);
+ }

@bottomupper_classname_assignment@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
+ int newParameterVariable1 = 0;
+ tempFunctionReturnValue = classIden.fromHtml(iden0,newParameterVariable1);
+ } else {
tempFunctionReturnValue = classIden.fromHtml(iden0);
+ }

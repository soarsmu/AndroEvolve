@upperbottom_classname@
expression exp0, exp1;
identifier iden0, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
classIden.setTextAppearance(iden0);
+ } else {
+ Context newParameterVariable0 = context;
+ int newParameterVariable1 = iden0;
+ classIden.setTextAppearance(newParameterVariable0,newParameterVariable1);
+ }

@bottomupper_classname@
expression exp0, exp1, exp2;
identifier iden0, iden1, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
+ int newParameterVariable0 = iden1;
+ classIden.setTextAppearance(newParameterVariable0);
+ } else {
classIden.setTextAppearance(iden0, iden1);
+ }

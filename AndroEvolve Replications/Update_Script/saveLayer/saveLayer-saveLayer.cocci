@upperbottom_classname@
expression exp0, exp1, exp2, exp3, exp4, exp5;
identifier iden0, iden1, iden2, iden3, iden4, classIden;
@@
...
+ if (VERSION.SDK_INT >= 21) {
 classIden.saveLayer(iden0, iden1, iden2, iden3, iden4);
+ } else {
+ int newParameterVariable5 = 31;
+ classIden.saveLayer(iden0, iden1, iden2, iden3, iden4,newParameterVariable5);
+ }

@bottomupper_classname@
expression exp0, exp1, exp2, exp3, exp4, exp5, exp6;
identifier iden0, iden1, iden2, iden3, iden4, iden5, classIden;
@@
...
+ if (VERSION.SDK_INT >= 21) {
+ classIden.saveLayer(iden0, iden1, iden2, iden3, iden4);
+ } else {
 classIden.saveLayer(iden0, iden1, iden2, iden3, iden4, iden5);
+ }


@upperbottom_classname_assignment@
expression exp0, exp1, exp2, exp3, exp4, exp5;
identifier iden0, iden1, iden2, iden3, iden4, classIden;
@@
...
+ if (VERSION.SDK_INT >= 21) {
tempFunctionReturnValue = classIden.saveLayer(iden0, iden1, iden2, iden3, iden4);
+ } else {
+ int newParameterVariable5 = 31;
+ tempFunctionReturnValue = classIden.saveLayer(iden0, iden1, iden2, iden3, iden4,newParameterVariable5);
+ }

@bottomupper_classname_assignment@
expression exp0, exp1, exp2, exp3, exp4, exp5, exp6;
identifier iden0, iden1, iden2, iden3, iden4, iden5, classIden;
@@
...
+ if (VERSION.SDK_INT >= 21) {
+ tempFunctionReturnValue = classIden.saveLayer(iden0, iden1, iden2, iden3, iden4);
+ } else {
tempFunctionReturnValue = classIden.saveLayer(iden0, iden1, iden2, iden3, iden4, iden5);
+ }

@upperbottom_classname@
expression exp0, exp1, exp2, exp3, exp4;
identifier iden0, iden1, iden2, iden3, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
 classIden.startDragAndDrop(iden0, iden1, iden2, iden3);
+ } else {
+ classIden.startDrag(iden0, iden1, iden2, iden3);
+ }

@bottomupper_classname@
expression exp0, exp1, exp2, exp3, exp4;
identifier iden0, iden1, iden2, iden3, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
+ classIden.startDragAndDrop(iden0, iden1, iden2, iden3);
+ } else {
 classIden.startDrag(iden0, iden1, iden2, iden3);
+ }


@upperbottom_classname_assignment@
expression exp0, exp1, exp2, exp3, exp4;
identifier iden0, iden1, iden2, iden3, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
tempFunctionReturnValue = classIden.startDragAndDrop(iden0, iden1, iden2, iden3);
+ } else {
+ tempFunctionReturnValue = classIden.startDrag(iden0, iden1, iden2, iden3);
+ }

@bottomupper_classname_assignment@
expression exp0, exp1, exp2, exp3, exp4;
identifier iden0, iden1, iden2, iden3, classIden;
@@
...
+ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
+ tempFunctionReturnValue = classIden.startDragAndDrop(iden0, iden1, iden2, iden3);
+ } else {
tempFunctionReturnValue = classIden.startDrag(iden0, iden1, iden2, iden3);
+ }

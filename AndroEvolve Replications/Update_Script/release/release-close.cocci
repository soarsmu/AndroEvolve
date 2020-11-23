@upperbottom_classname@
expression exp0;
identifier classIden;
@@
...
+ if (VERSION.SDK_INT < VERSION_CODES.N) {
classIden.close();
+ } else {
+ classIden.release();
+ }

@bottomupper_classname@
expression exp0;
identifier classIden;
@@
...
+ if (VERSION.SDK_INT < VERSION_CODES.N) {
+ classIden.close();
+ } else {
classIden.release();
+ }

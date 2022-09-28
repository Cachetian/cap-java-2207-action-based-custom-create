namespace my.bookshop;

using {
  sap,
  managed,
  cuid
} from '@sap/cds/common';

entity Books : cuid, managed {
  bookNo   : Integer;
  title    : String(100);
  stock    : Integer;
  comment  : String(200);
  bookType : Association to one BookTypes;
}

entity BookTypes : sap.common.CodeList {
  key code    : String(30);
      comment : String(200);
}

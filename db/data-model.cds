namespace my.bookshop;

using {
  managed,
  cuid
} from '@sap/cds/common';

entity Books : cuid, managed {
  bookNo  : Integer;
  title   : String(100);
  stock   : Integer;
  comment : String(200);
}

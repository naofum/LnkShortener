package de.hirtenstrasse.michael.lnkshortener;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class LinksDataSource {

    private SQLiteDatabase database;
    private LinksSqlLiteHelper dbHelper;
    private String[] allColumns = {LinksSqlLiteHelper.COLUMN_ID, LinksSqlLiteHelper.COLUMN_ADDED,
                                LinksSqlLiteHelper.COLUMN_SHORT_LINK, LinksSqlLiteHelper.COLUMN_LONG_LINK};

    public LinksDataSource(Context context){
        dbHelper = new LinksSqlLiteHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Link createLink(String longLink, String shortLink){
        ContentValues values = new ContentValues();

        values.put(LinksSqlLiteHelper.COLUMN_LONG_LINK, longLink);
        values.put(LinksSqlLiteHelper.COLUMN_SHORT_LINK, shortLink);
        long insertID = database.insert(LinksSqlLiteHelper.TABLE_LINKS, null, values);

        Cursor cursor = database.query(LinksSqlLiteHelper.TABLE_LINKS, allColumns, LinksSqlLiteHelper.COLUMN_ID + " = " + insertID,
                null, null, null, null);
        cursor.moveToFirst();

        Link newLink = cursorToLink(cursor);
        cursor.close();

        return newLink;

    }
    public void deleteLink(Link link) {
        long id = link.getId();
        database.delete(LinksSqlLiteHelper.TABLE_LINKS, LinksSqlLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Link> getAllLinks() {
        List<Link> links = new ArrayList<Link>();

        Cursor cursor = database.query(LinksSqlLiteHelper.TABLE_LINKS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Link link = cursorToLink(cursor);
            links.add(link);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return links;
    }

    private Link cursorToLink(Cursor cursor) {
        Link link = new Link();
        link.setId(cursor.getLong(0));
        link.setLongLink(cursor.getString(1));
        link.setShortLink(cursor.getString(2));
        link.setAdded(cursor.getLong(3));
        return link;
    }



}

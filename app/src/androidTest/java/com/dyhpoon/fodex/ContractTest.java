package com.dyhpoon.fodex;

import android.content.ContentUris;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.dyhpoon.fodex.data.actual.FodexContract;

import java.util.Arrays;
import java.util.List;

/**
 * Created by darrenpoon on 2/4/15.
 */
public class ContractTest extends AndroidTestCase {

    public void testBuildImageUri() {
        long id = 0;
        Uri uri = FodexContract.ImageEntry.buildImageUri(id);
        assertEquals(ContentUris.withAppendedId(FodexContract.ImageEntry.CONTENT_URI, id), uri);
    }

    public void testBuildTagUri() {
        long id = 0;
        Uri uri = FodexContract.TagEntry.buildTagUri(id);
        assertEquals(ContentUris.withAppendedId(FodexContract.TagEntry.CONTENT_URI, id), uri);
    }

    public void testSearchTagName() {
        String expected = "hello";
        Uri uri = FodexContract.TagEntry.buildSearchTagName(expected);
        String actual = FodexContract.TagEntry.getSearchTagName(uri);
        assertEquals(expected, actual);
    }

    public void testTagName() {
        String expected = "morning";
        Uri uri = FodexContract.TagEntry.buildTagName(expected);
        String actual = FodexContract.TagEntry.getTagName(uri);
        assertEquals(expected, actual);
    }

    public void testBuildImageTagUri() {
        long id = 0;
        Uri uri = FodexContract.ImageTagEntry.buildImageTagUri(id);
        assertEquals(ContentUris.withAppendedId(FodexContract.ImageTagEntry.CONTENT_URI, id), uri);
    }

    public void testTagNames() {
        List<String> expected = Arrays.asList("hello");
        Uri uri = FodexContract.ImageTagEntry.buildTagNames(expected);
        List<String> actual = FodexContract.ImageTagEntry.getTagNames(uri);
        assertEquals(expected, actual);

        expected = Arrays.asList("hello", "morning");
        uri = FodexContract.ImageTagEntry.buildTagNames(expected);
        actual = FodexContract.ImageTagEntry.getTagNames(uri);
        assertEquals(expected, actual);
    }

    public void testSearchId() {
        long expected = 0;
        Uri uri = FodexContract.ImageTagEntry.buildSearchId(expected);
        long actual = Long.parseLong(FodexContract.ImageTagEntry.getSearchId(uri));
        assertEquals(expected, actual);
    }

    public void testSearchName() {
        String expected = "hello";
        Uri uri = FodexContract.ImageTagEntry.buildSearchName(expected);
        String actual = FodexContract.ImageTagEntry.getSearchName(uri);
        assertEquals(expected, actual);
    }

    public void testBuildShareUri() {
        long id = 0;
        Uri uri = FodexContract.ShareEntry.buildShareUri(id);
        assertEquals(ContentUris.withAppendedId(FodexContract.ShareEntry.CONTENT_URI, id), uri);
    }
}

package de.hirtenstrasse.michael.lnkshortener;

// Copyright (C) 2017 Michael Achmann

//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

public class Link {
    private long id, added;
    private String originalLink, shortLink;

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getAdded(){
        return added;
    }

    public void setAdded(long added){
        this.added = added;
    }

    public String getLongLink(){
        return originalLink;
    }

    public void setLongLink(String originalLink){
        this.originalLink = originalLink;
    }

    public String getShortLink(){
        return shortLink;
    }

    public void setShortLink(String shortLink){
        this.shortLink = shortLink;
    }

}

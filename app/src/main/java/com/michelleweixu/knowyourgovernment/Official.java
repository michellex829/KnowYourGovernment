package com.michelleweixu.knowyourgovernment;

import java.io.Serializable;
import java.util.Objects;

public class Official implements Comparable<Official>, Serializable {
    // Add getters and make variables private
    String office;
    String name;
    String party;
    String officeAddress;
    String phoneNumber;
    String emailAddress;
    String website;

    //Social Media (only display data that is provided).
    //All are clickable – implicit intents.
    String facebook;
    String twitter;
    String youtube;
    String photoURL;

    public Official( String office, String name, String party, String officeAddress, String phoneNumber,
            String emailAddress, String website, String facebook,String twitter, String youtube, String photoURL) {
        this.office = office;
        this.name = name;
        this.party = party;
        this.officeAddress = officeAddress;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.website = website;
        this.facebook = facebook;
        this.twitter = twitter;
        this.youtube = youtube;
        this.photoURL = photoURL;
    }

    @Override
    public int compareTo(Official off) { // to sort and display officials by last names alphabetically
        String temp[] = off.name.split(" ");
        String offLastName = temp[temp.length - 1];
        temp = this.name.split(" ");
        String thisLastName = temp[temp.length - 1];

        return thisLastName.compareTo((offLastName));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Official off = (Official) o;
        return name.equals(off.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
package com.mj.poker.util;


import com.mj.poker.Const;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GetTextRecord {
    public static void main(String[] args) {
    }

    public static String getATxt(String domain) {
        List<String> got = new ArrayList<>();
        for(String server : Const.dnsServers) {
            new Thread(() -> {
                String result = getATxt(domain, server);
                if (!Utils.stringIsEmpty(result)) {
                    got.add(result);
                }
            }).start();
        }
        AtomicBoolean wait = new AtomicBoolean(true);
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            wait.set(false);
        }).start();
        while (wait.get()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                break;
            }
            if (!got.isEmpty()) {
                return got.get(0);
            }
        }
        return "";
    }

    public static String getATxt(String domain, String dnsServer) {
        try {
            Lookup lookup = new Lookup(domain, Type.TXT);
            if(!Utils.stringIsEmpty(dnsServer))
                lookup.setResolver(new SimpleResolver(dnsServer));
            Record[] records = lookup.run();
            if (records==null||records.length==0)
                return "";
            TXTRecord txtRecord = (TXTRecord) records[0];
            StringBuilder result = new StringBuilder();
            for (String str : (List<String>)txtRecord.getStrings()) {
                result.append(str);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
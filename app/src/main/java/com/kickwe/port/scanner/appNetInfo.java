package com.kickwe.port.scanner;

import java.util.ArrayList;


public class appNetInfo {
    private int UID;
    private String name = "";
    private ArrayList<String> tcp = new ArrayList();
    private ArrayList<String> tcp6 = new ArrayList();
    private ArrayList<String> udp = new ArrayList();
    private ArrayList<String> udp6 = new ArrayList();

    public appNetInfo(int UID, String name) {
        this.UID = UID;
        this.name = name;
    }


    public String toString() {
        if (UID == -1) {
            return this.name;
        }
        StringBuilder output = new StringBuilder();
        output.append("" +this.name +((this.name == null)?(" UID: " +this.UID):"") +"\n");
        for (String s : tcp) {
            String[] d = s.trim().split("\\s+");
            output.append("Local: ");
            output.append(d[2] +"\n");
            output.append("Foreign: ");
            output.append(d[3] +"\n");
            output.append("State: ");
            output.append(d[4] +"\n");
            output.append("Protocol: TCP\n");
        }
        for (String s : tcp6) {
            String[] d = s.trim().split("\\s+");
            output.append("Local: ");
            output.append(d[2] +"\n");
            output.append("Foreign: ");
            output.append(d[3] +"\n");
            output.append("State: ");
            output.append(d[4] +"\n");
            output.append("Protocol: TCP6\n");
        }
        for (String s : udp) {
            String[] d = s.trim().split("\\s+");
            output.append("Local: ");
            output.append(d[2] +"\n");
            output.append("Foreign: ");
            output.append(d[3] +"\n");
            output.append("State: ");
            output.append(d[4] +"\n");
            output.append("Protocol: UDP\n");
        }
        for (String s : udp6) {
            String[] d = s.trim().split("\\s+");
            output.append("Local: ");
            output.append(d[2] +"\n");
            output.append("Foreign: ");
            output.append(d[3] +"\n");
            output.append("State: ");
            output.append(d[4] +"\n");
            output.append("Protocol: UDP6\n");
        }
        return output.toString();
    }

    public int getUID() {
        return this.UID;
    }

    public String getName() {
        return (this.name == null)?" ":this.name;
    }
    public void addTcp (String tcp) {
        this.tcp.add(tcp);
    }

    public void addTcp6 (String tcp6) {
        this.tcp6.add(tcp6);
    }

    public void addUdp (String udp) {
        this.udp.add(udp);
    }

    public void addUdp6 (String udp6) {
        this.udp6.add(udp6);
    }

    public ArrayList<String> getTcp() {
        return this.tcp;
    }

    public ArrayList<String> getTcp6() {
        return this.tcp6;
    }

    public ArrayList<String> getUpd () {
        return this.udp;
    }

    public ArrayList<String> getUdp6 () {
        return this.udp6;
    }

}

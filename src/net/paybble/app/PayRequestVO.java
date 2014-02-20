package net.paybble.app;

public class PayRequestVO {
    public PayRequestVO(String paybbleTrx, int cmd) {
        id = paybbleTrx;
        status = cmd;
    }

    String id;// "74B297FF-E7D1-42BB-9218-0EF70C7ADD4E",
    String deviceid;// "FEEA9D3E-1EF3-49E9-9D0A-B16E54F4661D",
    String posid;// "1EF1E0DA-1DBF-4628-84BA-5B217A32E314",
    Double amount;// 16.4,
    String description1;// "Lobby Demo",
    String description2;// "undefined",
    int status;// -1
}

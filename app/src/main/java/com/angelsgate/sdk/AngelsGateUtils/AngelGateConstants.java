
package com.angelsgate.sdk.AngelsGateUtils;


public final class AngelGateConstants {

    public static String STARTFOREGROUND_ACTION = "com.angelgate.action.startforeground";
    public static String STOPFOREGROUND_ACTION = "com.angelgate.action.stopforeground";


    ///////////////////////
    public static String NOTIFICATION_CHANNEL_ID ="AngelGate";
    public static String NOTIFICATION_CHANNEL_NAME = "AngelGate Channel";
    public static String NOTIFICATION_CHANNEL_DESCRIPTION = "AngelGate";


    /////////////////////////////

    public static String iv;
    public static String secretkey;
    public static String publicKey;
    public static int MintLengthSsalt;
    public static int MaxLengthSsalt;


     public static String ServerIv;
    public static String ServerpublicKey;


    public static String PreAuthMethodName;
    public static String PostAuthMethodName;
    public static String SignalMethodName;
    public static String RetrofiteBaseUrl;

    private AngelGateConstants(AngelGateConstantsBuilder builder) {

        this.publicKey = builder.publicKey;
        this.iv = builder.iv;
        this.secretkey = builder.secretkey;
        this.MintLengthSsalt = builder.MintLengthSsalt;
        this.MaxLengthSsalt = builder.MaxLengthSsalt;


         this.ServerIv = builder.ServerIv;
        this.ServerpublicKey = builder.ServerpublicKey;


        this.PreAuthMethodName = builder.PreAuthMethodName;
        this.PostAuthMethodName = builder.PostAuthMethodName;
        this.SignalMethodName = builder.SignalMethodName;
        this.RetrofiteBaseUrl = builder.RetrofiteBaseUrl;
    }



    //Builder Class
    public static class AngelGateConstantsBuilder {

        private String iv = "";
        private String secretkey = "";
        private String publicKey = "";

         private String ServerIv = "";
        private String ServerpublicKey = "";


        private int MintLengthSsalt = 8;
        private int MaxLengthSsalt = 9;

        public static String PreAuthMethodName = "preAuth";
        public static String PostAuthMethodName = "postAuth";
        public static String SignalMethodName = "signal";
        public static String RetrofiteBaseUrl = "";

        public AngelGateConstantsBuilder(String publicKey, String iv, String secretkey, String LoginMethodName, String VerifyLoginMethodName, String BaseUrl) {
            this.publicKey = publicKey;
            this.iv = iv;
            this.secretkey = secretkey;
            this.PreAuthMethodName = LoginMethodName;
            this.PostAuthMethodName = VerifyLoginMethodName;
            this.RetrofiteBaseUrl = BaseUrl;
        }


        public AngelGateConstantsBuilder setMintLengthSsalt(int mintLengthSsalt) {
            this.MintLengthSsalt = mintLengthSsalt;
            return this;
        }

        public AngelGateConstantsBuilder setMaxLengthSsalt(int maxLengthSsalt) {
            this.MaxLengthSsalt = maxLengthSsalt;
            return this;
        }


        public AngelGateConstants build() {
            return new AngelGateConstants(this);
        }

    }
}

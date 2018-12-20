# Google-Map-Floating-Info-Window-Uber

An Android application to draw polyline with floating info window using route_key encoded string.


    @Override
     public void onMapReady(GoogleMap googleMap) {

         PolyUtils polyUtils = new PolyUtils(this, googleMap, "{punAgqyhNIgAWAeE[iBKLh@VdA\\z@t@rALr@SrDdC@BHj@lDPvB?t@Ax@DRBf@Df@f@lFdHcBp@QnDy@jBi@bCiA|E_CrDaBhAu@BIWQ_BmCQF_C~@");
         polyUtils.setSourceAddress(new MyAddress("Prestige palladium bayan", "4 mins"));
         polyUtils.setDestinationAddress(new MyAddress("Anna nagar west", null));
         polyUtils.start();

     }

Youtube Link https://youtu.be/ld8XkVr78x8
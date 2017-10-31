# Android StealthyCamera
 A library that allows you to take camera pictures without any UI. This can be triggered from a service or any other threads.
## Installation

 Project level `build.gradle` add `flatDir`
```
allprojects {
   repositories {
      jcenter()
      flatDir {
        dirs 'libs'
      }
   }
}
```

Then in app level `build.gradle` add

```
dependencies {
       compile(name:'cards', ext:'aar')
}
```
## Dependencies
`com.android.support:appcompat-v7:26.+`
## Usage
```java
StealthyCamera stealthyCamera=new StealthyCamera(getApplicationContext());

        /* (Optional) Listener for complete or error events */
        stealthyCamera.setOnActionListener(new StealthyCamera.OnActionListener() {
            @Override
            public void onPictureTaken() {

            }

            @Override
            public void onPictureTaken(byte[] data) {

            }

            @Override
            public void onPictureTaken(Bitmap bitmap) {

            }

            @Override
            public void onError(Exception e) {

            }
        });

        /* (Optional) Bitmap options */
        // Default: 70, 100 is best quality
        stealthyCamera.setImageCompressionRatio(70);

        /* (Optional) Output file options */
        // Default: True, if true when calling request permission the user will be asked to approve write permission
        // if requestPermissions is called
        stealthyCamera.setWriteToFileOnFinish(true);
        // Default: .jpg, add another extension if you don't want it to appear in the gallery
        stealthyCamera.setFileExtension(".jpg");
        // Default: StealthyCamera, The directory to write to on sdcard
        stealthyCamera.setDirectoryName("MyDir");
        // Default: false
        stealthyCamera.setVerbose(true);

         /* Make sure you have the CAMERA permission and WRITE_EXTERNAL_STORAGE if setWriteToFileOnFinish is true */
        if(stealthyCamera.requiresPermissions(this))
            stealthyCamera.requestPermissions(this);
        else {

        stealthyCamera.selectFrontCamera();
        stealthyCamera.takeShot();
        }
```


## Credits
Noräs Salman (Creator)
## License
APACHE 2.0 license

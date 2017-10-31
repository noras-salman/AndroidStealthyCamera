# Android StealthyCamera
 A library that allows you to take camera pictures without any UI. This can be triggered from a service or any other threads. 

## Tested on android versions:
Ice Cream Sandwich 4.0 – 4.0.4	

Jelly Bean 4.1 – 4.3.1

KitKat 4.4 – 4.4.4

Lollipop 5.0 – 5.1.1

Marshmallow 6.0 – 6.0.1

Nougat 7.0 – 7.1.2

Oreo 8.0

## Installation

#### Option 1
Add the `stealthycamera-debug.aar` to your `libs` directory 

In project level `build.gradle` add `flatDir`
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
       compile(name:'stealthycamera-debug', ext:'aar')
}
```

#### Option 2
From android studio, Go to File > New module > Impor .jar/.aar file
## Dependencies
`com.android.support:appcompat-v7:26.+`
## Usage
Add the permissions to the Manifest 
```
<uses-permission android:name="android.permission.CAMERA"></uses-permission>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
```

If yout activity:
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

If you don't want to use this from an activity make sure to ask for ther permissions your self.

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D

## Credits

Noräs Salman (Creator)



## License

This project is licensed under the APACHE 2.0 License - see the [LICENSE](LICENSE) file for details  

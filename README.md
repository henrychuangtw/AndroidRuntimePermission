# RuntimePermission
Android RuntimePermission, example of Dangerous Permissions and Special Permissions
<br/><br/>
In my practical experience, I think it is better to use common activity to request permission, 
advantage of this design pattern :<br/>
1. No need to add RuntimePermission code in every activity or fragment.<br/>
2. If RuntimePermission code need to edit later, just edit common activity.<br/>
3. Avoiding bugs with nested fragment(ex: <a href="https://code.google.com/p/android/issues/detail?id=189121" target="_blank">Issue 189121</a>).

<br/><br/>

Dangerous Permissions(Ex: in activity or fragment) and Special Permissions(Ex: setRingtone)<br/>
![](app/src/main/assets/main.png)
<br/><br/>

permission dialog<br/>
![](app/src/main/assets/PermissionDialog.png)
<br/><br/>

show UI with rationale of requesting this permission<br/>
![](app/src/main/assets/PermissionRationale.png)
<br/><br/>

once user deny permission, next time will see "Nerver ask again"<br/>
![](app/src/main/assets/NeverAskAgain.png)
<br/><br/>

open your app setting when user chose "Nerver ask again"<br/>
![](app/src/main/assets/AppSetting.png)
<br/><br/>

request multiple permissions<br/>
![](app/src/main/assets/MultiplePermissions.png)
<br/><br/>

use common activity to request permission<br/>
![](app/src/main/assets/common_RequestPermission.png)

# Test Device
HTC One A9, Android 6.0
<br/>
HTC One X, Android 4.2.2


# Reference
<a href="http://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en" target="_blank">http://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en</a>
<br/>
<a href="http://stackoverflow.com/questions/30719047/android-m-check-runtime-permission-how-to-determine-if-the-user-checked-nev" target="_blank">http://stackoverflow.com/questions/30719047/android-m-check-runtime-permission-how-to-determine-if-the-user-checked-nev</a>
<br/>
<a href="https://developer.android.com/training/permissions/index.html" target="_blank">https://developer.android.com/training/permissions/index.html</a>
<br/>
<a href="https://developer.android.com/guide/topics/security/permissions.html#normal-dangerous" target="_blank">https://developer.android.com/guide/topics/security/permissions.html#normal-dangerous</a>

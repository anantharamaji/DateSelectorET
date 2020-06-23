## DateSelectorET :octocat: :date:
An Android library for Date Picker. 

DateSelectorET is an EditText that provides an option to display a date picker on click event.

### Usage
Define DateSelectorET in layout.
```
<org.github.date.DateSelectorET
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:hint="Select date"
  app:dateFormat="ddmmyyyy"
  app:layout_constraintEnd_toEndOf="parent"
  app:layout_constraintStart_toStartOf="parent"
  app:layout_constraintTop_toTopOf="parent" />
```

### Dependency
Add it in your root build.gradle at the end of repositories:
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Add the dependency
```
implementation 'com.github.anantharamaji:DateSelectorET:1.0.1'
```

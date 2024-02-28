# Installation

## Gradle
Add the dependency from Maven Central to your app module's (not top-level) `build.gradle` file:

```groovy
dependencies {
    implementation 'dev.hotwire:strada:<latest-version>'
}
```

[![Download](https://img.shields.io/maven-central/v/dev.hotwire/strada)](https://search.maven.org/artifact/dev.hotwire/strada)

See the [latest version](https://search.maven.org/artifact/dev.hotwire/strada) available on Maven Central.

**Note:** `strada-android` works seamlessly with [turbo-android](https://github.com/hotwired/turbo-android) and the documentation provides instructions for integrating Strada with your [Turbo Native](https://turbo.hotwired.dev/handbook/native) app. Keep in mind that `turbo-android` is not automatically included as a dependency in `strada-android`, so you'll want to setup your `turbo-android` app first.

## Required `minSdkVersion`
Android SDK 26 (or greater) is required as the `minSdkVersion` in your app module's `build.gradle` file:
```groovy
defaultConfig {
    minSdkVersion 26
    ...
}
```

# Pre-release Builds
Pre-release builds will be published to [GitHub Packages](https://github.com/features/packages).

## Personal Access Token
If you'd like to use a pre-release version, you'll need to create a [Personal Access Token](https://docs.github.com/en/free-pro-team@latest/packages/learn-github-packages/about-github-packages#authenticating-to-github-packages) in your GitHub account and give it the `read:packages` permission.

Copy your access token to your `.bash_profile` (or another accessible place that's outside of source control):

```bash
export GITHUB_USER='<your username>'
export GITHUB_ACCESS_TOKEN='<your personal access token>'
```

##  Gradle
Add the GitHub Packages maven repository and the dependency to your app module's `build.gradle` file:

```groovy
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/hotwired/strada-android")

        credentials {
            username = System.getenv('GITHUB_USER')
            password = System.getenv('GITHUB_ACCESS_TOKEN')
        }
    }
}

dependencies {
    implementation 'dev.hotwire:strada:<pre-release-version>'
}
```

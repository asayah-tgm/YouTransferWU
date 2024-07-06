# Data Science and Artificial Intelligence II

Ownership, Control, and Access - Decentralised Web

Sayah Karadji, Arian

Crozat, Baptiste

## About the project

This project investigates the usability of the [Solid](https://solidproject.org/) specification to create a decentralized file-sharing alternative.

The goal is to create a system that lets users exchange files securely and privately from their Pods while retaining total control over the data and modalities.

## Getting started - your Pod and credentials

Solid itself is a specification and as such, does not host any data nor credentials. To get the latter, you must register to one of the [Pod Providers recommended by Solid](https://solidproject.org/users/get-a-pod#get-a-pod-from-a-pod-provider), or run your own Solid server, that you can create from any regular (local or remote) server through installing a [set of libraries from Solid](https://docs.inrupt.com/developer-tools/javascript/client-libraries/tutorial/getting-started/#install-the-client-libraries).

Solid credentials consist of a WebID, which is an URL including the username (like https://id.inrupt.com/{username}), and a Pod Storage, which is a URL as well including a Pod Identifier (like https://storage.inrupt.com/{Pod Identifier}).

We choose to register at Inrupt, which is the company co-founded by Sir Tim Berners-Lee behind Solid, and provide some documentation to develop Solid-based applications. Registration is simply done through [Inrupt PodSpaces](https://docs.inrupt.com/pod-spaces/) by providing an email address, choosing a username and defining a password. Once done, the user can log in and access a page where their WebID and Pod Storage are displayed. The WebID is like a handle that let you connect with other Pods Users. The Pod Storage is the URL where your files are stored. In theory, because Solid is a specification, you should be able to move your Pod and its stored files to another Pod Provider if you wish. However, for the scope of this project, we simply moved forward using Inrupt.

## Building an application

Once you have your Pod and credentials, the next step (in the developer's way) is to create an application to use them. Recall the goal of this project is to build a simple custom file-sharing application to test the usability of the Solid ecosystem.

We initially followed the [Inrupt tutorial](https://docs.inrupt.com/developer-tools/javascript/client-libraries/tutorial/getting-started/) to develop a Solid application in JavaScript. However, after a few iterations, we encountered limitations with the Solid JavaScript libraries, which provide higher-level modules that are not easily customizable.

Thus, we switched to using Java, where we had more success due to its greater flexibility and customization potential. We used [IntelliJ IDEA](https://www.jetbrains.com/idea/), a user-friendly IDE for Java (and Kotlin), to develop our application. We also used [Gradle](https://gradle.org/), an open-source build automation tool for software development useful for tasks such as compilation, testing and deployment.

We went through quite a few iterations, looking at the [Inrupt documentation](https://docs.inrupt.com/developer-tools/java/client-libraries/) and found help in the [community forum](https://forum.solidproject.org/) where other people share about Solid projects they built. Consult the 'Resources' section to see some of these, or explore [our GitHub repository](https://github.com/asayah-tgm/YouTransferWU).

## Running our application - YouTransfer

Follow this process to launch the YouTransfer application.

### Prerequisites

1. Retrieve the YouTransfer application `.jar` file by downloading it from [our GitHub repository under 'Releases'](https://github.com/asayah-tgm/YouTransferWU/releases/). Tip: the current file name should be `YouTransfer-V1.jar`.

2. Check if Java version >=17 is installed by running `java -version` in your terminal. We recommend using Windows PowerShell for Windows or Terminal for macOS to follow along with this tutorial.

3. If needed, install Java version >=17. You can download it here:
- Windows: https://corretto.aws/downloads/latest/amazon-corretto-17-x64-windows-jdk.msi.
- macOS: https://corretto.aws/downloads/latest/amazon-corretto-17-x64-macos-jdk.pkg.

### Launching the application

4. Once Java version >=17 is installed properly, navigate to the directory containing the `.jar` file. Tip: use `cd <path to the directory>` in your terminal.

5. Set the environment to 'dev' by running:
- Windows: `$env:SPRING_PROFILES_ACTIVE="dev"` in Windows PowerShell.
- macOS: `export SPRING_PROFILES_ACTIVE="dev"` in Terminal.

6. Build and launch the application by running in `java -jar YouTransfer-V1.jar` in the terminal. You might need to allow communication permission. Tip: if that does not work, double check the application file name &rarr; the `.jar` file that you downloaded. If needed, replace it accordingly in the line of code. Be also sure to have Java version >= 17 installed on your machine as explained in the 'Prerequisite' section above.

7. Open the browser of your choice and go to `http://localhost:19999`. Note : in some cases, it falls back to port `8080`. However, you can check the terminal to see which port is in use.

8. You should now be seeing the application login page and be able to do the actions described in the 'Results' section below.

## Exiting the application

9. Hit `ctrl` + `c` in the terminal to exit the YouTransfer application.

## Building the application locally

To build (and the run) the application in your own environment if you wish to do so, you will need the following:
- Java Version 17.0 to ensure compatibility,
- Gradle 8.8 to build and run the task.

## Results

The project resulted in a simple yet functioning app that enables users to log in to their Pods (through Inrupt authentification), see their data on their Pods (including images), send access requests to other users' Pods, grant (or deny) access requests they received, as well as remove access if they wish to do so. The application was functioning for multiple users and tested using four different WebIDs.

## Futur work

Future work might try to bring this project further by expanding it to other Pod Providers, improving the data (image) display, testing the handling of other (and larger) types of data such as videos, and upgrading the UI overall.

## Conclusion

This project demonstrated the potential of the Solid specification for creating a decentralized file-sharing system, enabling a secure and private file exchange while maintaining user control over data. This resulting YouTransfer application, tested with multiple users, allows login, data viewing, access requests, and permissions management within the Solid ecosystem.

## Acknowledgments

We thank Professor Dr. Sabrina Kirrane from the Vienna University of Economics and Business (WU) for supporting us with this project as part of her Data Science and Artificial Intelligence II course from the MSc in Digital Economy.

## Resources

- [Sayah, A. & Crozat, B. - YouTransfer (GitHub repository)](https://github.com/asayah-tgm/YouTransferWU)
- [Why Solid?](https://noeldemartin.com/blog/why-solid): a great blogpost highlighting the advantages and inconvenients of using Solid.
- [Getting started as a Solid developer](https://solidproject.org//developers/tutorials/getting-started)
- [Inrupt - Getting started](https://docs.inrupt.com/developer-tools/javascript/client-libraries/tutorial/getting-started/)
- [Inrupt Java Client Libraries](https://docs.inrupt.com/developer-tools/java/client-libraries/): Inrupt's documentation for development in Java.
- [Inrupt PodSpaces Login](https://start.inrupt.com/profile): Pod Provider that was used in this project.
- [Inrupt Identity Provider](https://login.inrupt.com/): OpenID Provider that was used in this project.
- [Inrupt WebID Profile Editor](https://id.inrupt.com/): WebID Provider that was used in this project.
- [Kulha, R. & El Sirfy - AccessArt (GitHub repository)](https://github.com/RobbsX/AccessArt): DSAI2 Solid project of 2023.
- [Solid OIDC Flow: Authorization Code Grant with PKCE Authorization Flow](https://solidproject.org/TR/oidc-primer#authorization-code-pkce-flow)
- [Solid authentication with Spring Boot and Spring Security](https://www.konsolidate.eu/stories/solid-spring): working implementation though without dynamic client registration.

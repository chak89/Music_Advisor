# Music Advisor

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)

## General info
Personal music advisor that makes preference-based suggestions and even shares links to new releases and featured playlists.  
Implemented using Spotify’s API, Java Generics, OAuth 2.0 and MVC pattern.  
https://hyperskill.org/projects/62
	
## Technologies
Project is created with:
* Java 11.0.8
* Gradle 7.1.1
	
## Setup
Replace member fields 'clientId' and 'clientSecret' in SpotifyModel.java with your own from Spotify API.
Compile and run as standard java code. 
Takes 3 arguments.  
`-access` argument should provide authorization server path. The default value should be https://accounts.spotify.com  
`-resource` argument should provide API server path. The default value should be https://api.spotify.com  
`-page` argument create a paginated output that will display the defined number of entries per page. If this argument is not set, the default value 5 will be used.  

Program arguments example:
```
-access "https://accounts.spotify.com" -resource "https://api.spotify.com" -page 15
```

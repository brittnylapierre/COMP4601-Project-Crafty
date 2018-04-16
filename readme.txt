Brittny Lapierre SID: 100922938
Kelly Maclauchlan SID: 100927176

To run the project you need to import it into eclipse. 
After this there are a few files that need to have their paths changed and their approximate locations : 

In Server String ROOT at line 88

And 

In productService String path at line 41



These both need to be changed to be pointing to the various files in the project folder. 

Before running make sure that you have mongo db setup on port 27017. 

This is the main end point to the service. 
http://localhost:8080/COMP4601_FINAL_SERVER/crafty/. 
The order to initially call the endpoints is: 
/parse
/generateUsers
And then you can continue from there to navigate the app through the UI.

# Welcome
This is my neural network I made.
It is designed to use the mnist data set converted to csv, which will
It was designed with the following priorities:
- As self sourced as possible. No calls made to any libraries that contribute to the neural net directly.
  - The caveat to this is that i am using external libraries when i find them useful for the project, like spotless or log4j. 
- This is a pet project, not a display of my ability to code in a production environment. Things i could do but choose not to include but are not limited to:
  - Use more robust UI Handling to assist with misuse.
- This was originally a solo college project where the instructions were basically comprised of: do something interesting, have fun. It has since morphed into an ongoing project ive continued years later as a way to hone/maintain skills.

Credit to http://neuralnetworksanddeeplearning.com for explaining the concepts in a way i could grasp, but with enough technical details to be able to implement myself.
As well as https://pjreddie.com/projects/mnist-in-csv/ for providing a good csv of the training data w/o any logins or fuss. 

Todo list:
- Multi Threading. Each element of a bucket can be parallelized.
- Extract the activation functions to config, allowing for multiple options. 
- Extract the backprop style code into a new class to allow for a sibling class implementing an evolution based approach.
- install gradle for log4j, to facilitate debug printing.
- Spring beans for config & NN factory. 
- An actual command line interface helper so i can set flags easily without having to modify logic extensively in driver every time i want to expose a feature to ui.


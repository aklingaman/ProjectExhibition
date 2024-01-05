# Welcome
This is my neural network I made.
It is designed to use the mnist data set converted to csv, which will
It was designed with the following priorities:
- As self sourced as possible. No calls made to any libraries that contribute to the neural net directly. 
- This is a pet project, not a display of my ability to code in a production environment. Things i could do but choose not to include but are not limited to: 
  - Force some way to include some more contrived testing, such as mockito.
  - Use more robust UI Handling to assist with misuse.
- This was originally a solo college project where the instructions were basically comprised of: do something interesting, have fun. It has since morphed into an ongoing project ive continued years later as a way to hone/maintain skills.

Todo list:
- Multi Threading. Each element of a bucket can be parallelized.
- Extract the activation functions to config, allowing for multiple options. 
- Extract the backprop style code into a new class to allow for a sibling class implementing an evolution based approach.
- install gradle for log4j, to facilitate debug printing. 


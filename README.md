# DLVfit

An application showcase of EmbASP framework for embedding ASP in complex systems. You can find the current development version at https://github.com/dave90/EmbASP

DLVfit is a health and fitness app that monitors the user activity during the day and suggests some workout plans that depend on her age, weight, gender and goals. The app periodically stores some information about the user activities (running, walking, etc.) and infers the current amount of calories burned so far. When the user asks for a workout plan, DLVfit proposes a set of exercises that would allow the user to reach her daily goal, taking into account also to her preferences. The suggested workout plans are computed in the background by DLV via EmbASP.

The ASP program used within DLVfit can be found at https://www.mat.unical.it/calimeri/projects/embasp/dlvfit/dlvfit_logic_program.lp. Basically, the program guesses for possible fitness exercises to do in order to burn the remaing calories. Each answer set represent a possible workout plan, in which is ensured that the user's requirements about the calories to burn and the time to spend in the workout are respected. Moreover, also user's preferences are taken into account by means of weak constraints.

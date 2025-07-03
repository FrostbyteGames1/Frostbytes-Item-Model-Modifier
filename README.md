[![Frostbyte's Item Model Modifier](https://github.com/user-attachments/assets/692713b2-8f4d-453a-b929-ac620778a534)](https://github.com/FrostbyteGames1/Frostbytes-Item-Model-Modifier)

[![CurseForge](https://cf.way2muchnoise.eu/short_849346_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/frostbytes-item-model-modifier)
[![Modrinth](https://img.shields.io/modrinth/dt/tBjxZ7JW?color=00AF5C&label=downloads&logo=modrinth)](https://modrinth.com/mod/frostbytes-item-model-modifier)
[![Requires Fabric API](https://img.shields.io/badge/Requires-Fabric%20API-dbd0b4?logo=data%3Aimage%2Fpng%3Bbase64%2CiVBORw0KGgoAAAANSUhEUgAAAfQAAAH0BAMAAAA5%2BMK5AAAAGFBMVEUAAADb0LTGvKW8spyuppSakn6Aem04NCogwuCRAAAAAXRSTlMAQObYZgAAAo1JREFUeNrt2zFOw0AQhlF3rg0FNbkBSpEakSMQcQPqBITm%2BnRIM4VHVkDG8L56m7ftvztIkiRJkiRJkiRJkrTJxsid0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0Ychcpcpdxc5dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHT0r952ufspF7kzOjo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ovpfpo%2BRa%2Fb0932uOY6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6OvmF6P6BPpca2zz1MuSiho6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6P%2FUnqUGlvzX73WvDyI%2Bc7o6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6OvSx0q9qpvdfIV6iNxrCR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR19ZXrkLlcM6H23xdrQa0MJHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHf2H6WPkjrnHbx3Q7xv6wsEcHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR19Lfop93zMTcvaL3s7MJTQ0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0Vv6qkXu45Q7lpbRD82Ajo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6%2BHfry%2Bb1exVOxdlR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR0dHR09Ja%2B4WL%2BKl4ih46Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6O%2Fi%2FptZhvKKGjo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojo6Ojt3RJkiRJkiRJkiRJkjbRJ0KLYiuU9T5SAAAAAElFTkSuQmCC)](https://modrinth.com/mod/fabric-api)

**Overview:**

This mod adds a ```/model``` command, allowing any item to be given any model. ```/model``` can be called with one of four arguments, as long as the player's main hand is not empty:
- ```/model get```: Returns the identifier of the model that is currently applied to the item
- ```/model set <identifier>```: Sets the item's model to the model defined by ```<identifier>```
- ```/model reset```: Resets the item to its vanilla model
- ```/model gui```: Opens a GUI which can be used to visually browse through every currently loaded item model

Items modified via the ```/model``` command remain 100% vanilla, so the custom model will render even for players who don't have [Frostbyte's Item Model Modifier](https://github.com/FrostbyteGames1/Frostbytes-Item-Model-Modifier) installed (as long as the model is vanilla or part of a texture pack that is shared by all players involved). Additionally, if the item being modified is a helmet, the new model will be rendered instead of the helmet texture.

**Using the GUI:**

When running the ```/model gui``` command while holding any item in the player's mainhand, a new Remodel screen will be opened. The two item slots at the top of the screen will display the current held item (left) and a preview of that item with the currently slected model applied (right).

Below these item slots are a search bar and a stonecutter-style scrollable list of every currently loaded item model, listed in alphabetical order by identifier.

The GUI also contains three buttons:
- A blue circular arrow (left): resets the held item to its vanilla model
- A green check mark (center): applies the currently selected model
- A red X (right): exits the GUI

![The /model GUI for an unmodified Diamond Helmet](https://github.com/user-attachments/assets/b3bcdda5-6f3a-4573-a684-e9239cee66fc)

If any of the models in the list are clicked, the preview item slot (right) will update to reflect the current selection. The button with the green check mark (center) will then become active, signifying that it can be clicked to apply the current selection and exit the GUI.

![The /model GUI for an unmodified Diamond Helmet with the Flowering Azalea Sapling model selected](https://github.com/user-attachments/assets/584f1183-77d9-4140-9bda-32bf26c663be)

If ```/model gui``` is run with an already modified item in the player's mainhand, the button with the blue circular arrow (left) will be active, signifying that it can be clicked to reset the current item to its vanilla model and exit the GUI.

![The /model GUI for an Diamond Helmet remodeled to look like a Flowering Azalea Sapling](https://github.com/user-attachments/assets/78f99b5b-2e34-4980-acda-5daca29d4382)

Once an item has been remodeled:
- No mod-dependent data will have been applied, meaning custom model will remain even if [Frostbyte's Item Model Modifier](https://github.com/FrostbyteGames1/Frostbytes-Item-Model-Modifier) is uninstalled 
- If the item is stackable, it will now only stack with other items with the same NBT data
- If the item can be equipped to the head slot, its new model will be rendered instead of the armor texture

![The suvival inventory of a player with the Ari default skin, wearing a Diamond Helmet remodeled to look like a Flowering Azalea Sapling](https://github.com/user-attachments/assets/f3e8cae3-f5d9-49be-8f60-c589163921af)

**FAQs:**

- *Will this be updated to 1.x.x?* - Yes! I plan on updating this mod for as long as I can. Expect to wait a few weeks after major Minecraft updates for an updated version of this mod. There may be longer pauses between minor versions depending on how busy I am.
- *Will you make a Forge version?* - No. I don't make Forge mods, but feel free to make your own (unofficial) port as long as you credit me for any code borrowed from this mod.
- *Can I use this in my mod pack?* - Yes! I made this mod for people to use!

**Download:**

Modrinth - https://modrinth.com/mod/frostbytes-item-model-modifier

Curseforge - https://www.curseforge.com/minecraft/mc-mods/frostbytes-item-model-modifier

[![BisectHosting](https://www.bisecthosting.com/partners/custom-banners/92987bf2-5957-4acb-8dd9-0fadd0fdd7e4.webp)](https://www.bisecthosting.com/frostbyte)

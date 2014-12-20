Anaphora Resolver
=================

Introduction
------------
This project is aimed at building an **Anaphora Resolver** for the Hindi 
Language. This is a part of a challenge faced in the field of Natural Language 
Processing. Anaphora is an expression that refers to another expression
(Antecedent) that has occured previously.

	Example
	Ram saw a laptop yesterday. He wants to buy it.
	
In the above example we have 2 anaphoras, *He* and *it*. Now, with our world 
knowledge we already know that *He* refers to *Ram* and similarly, *it* 
refers to *laptop*. To resolve such relations in a large text corpus (Hindi)
is called Anaphora Resolution

An anaphora in Hindi can be divided into 3 types (though not a stict
categorization).

1. Entity type anaphora (eg. usne, use, mein, vah, etc) which always refers to 
   an entity type word group such as Person, Organizations, Places, etc.
2. Event type anaphora (eg. yeh, iske baad, isliye, etc), which always refer to
   an event which are verbs in this case.
3. Unknown types (eg. sabhi, kabhi, etc)

Project Segments
---------------- 
This project is being completed in a number of segments.

1. Anaphora Classifier: To classify a giveb anaphora into the above three
   categories
2. Event Anaphora Resolver: To resolve the relations between the event anaphora
   and its antecedent, and also identify the modifiers of the antecendent.
3. Entity Anaphora Resolver: To resolve the relations between the entity 
   anaphora and its antecedent, then therby also indentifying its modifier
   to complete the span of the antecedent.

Tools built for Hindi language specific procedural programming
--------------------------------------------------------------

## SSF extraction tool

This tool has been developed to extract relavent data from the (visually
confusing!) SSF data files.

An SSF file contains all the morphological data pertaining to the words used
in the corpus, the dependency relations between the word groups used in the 
courpus and other relavent information such as name entity tags, reference to
the anaphoras (manually marked for developing the training data, etc).

So, each line can convey either one of the four informations:

1. Details of the file, such as the source and authur of the file
2. Indicator to the start or end of the sentence.
3. Features corresponding to the word groups (also called chunks)
4. Features corresponding to the words contained in the word groups

In preparing the training data for a learning algorithm, it is often
required to extract the features related to the word group and the 
words contained in the group (chunk). Hence this tool can be used to
extract such data easily by using few simple lines of codes:

1. Make an object of the class SSFextract to call all the functions
   associated with the class
	
	`SSFextract data = new SSFextract("SSF line");`

Now, this object will take an argument which is the line from which the data has
to be extracted.

2. Now, appropriate features can be extracted using this object, `data`.

	`String headChunkID = data.headChunkID;`   
  

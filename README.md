# midi2mbseq
mid file to mbseq (Arturia Microbrute sequence) converter

# features
* a long mid file will be split into 8 sequences of 64 steps each
* option to fill at the end the sequence with pauses to the nearest 16 multiple of the sequencer

# polyphonic midi notes
For polyphonic midi file each step of the sequencer will be filled with the first note in the midi message (if you play a chord just the highest note will be chosen).
Quantize is suggested but not needed.

# run 
download https://github.com/mebitek/midi2mbseq/blob/master/mid2mbseq-0.0.1.jar 

run as 
```
java -jar mid2mbseq-0.0.1.jar -i midi_file.mid
```
add -f to use filler option

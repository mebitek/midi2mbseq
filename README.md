# midi2mbseq
mid file to mbseq (Arturia Microbrute sequence) converter

http://music.mebitek.com/news/mid-file-mbseq-arturia-microbrute-sequence-converter-released

# features
* creaet mbseq file from file or from directory
* a long mid file will be split into 8 sequences of 64 steps each
* option to fill at the end the sequence with pauses to the nearest 16 multiple of the sequence length
* custom fill at end sequence value

## polyphonic midi notes
For polyphonic midi file each step of the sequencer will be filled with the first note in the midi message (if you play a chord just the highest note will be chosen).
Quantize is suggested but not needed.

# run 
download https://github.com/mebitek/midi2mbseq/blob/master/mid2mbseq-0.2.1.jar

run as 
```
java -jar mid2mbseq-0.2.1.jar -i midi_file.mid
```
output will be `midi_file.mbseq`

```
java -jar mid2mbseq-0.2.1.jar -d /home/user/midi_directory
```
output will be `midi_directory.mbseq`

add `-f` to use filler option
add `-c value` to use the filler with custom value

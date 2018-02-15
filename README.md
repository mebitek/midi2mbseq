# midi2mbseq
mid file to mbseq (Arturia Microbrute sequence) converter

http://music.mebitek.com/news/mid-file-mbseq-arturia-microbrute-sequence-converter-released

# features
* gui interface and batch mode
* generate mbseq file from file or from directory
* a long mid file will be split into 8 sequences 
* option to fill the sequence with pauses to the nearest 16 multiple of the sequence length
* option to fill the sequence with pauses to custom value
* option to fill the sequence with pauses to the max microbrute sequencer size (64)
* set custom sequence length

### polyphonic midi notes
For polyphonic midi file each step of the sequencer will be filled with the first note in the midi message (if you play a chord just the highest note will be chosen).
Quantize is suggested but not needed.

# run 
download https://github.com/com.mebitek/midi2mbseq/blob/master/mid2mbseq-0.3.jar

run as 
```
java -jar mid2mbseq-0.2.1.jar
```
to run in batch mode as 
```
java -jar mid2mbseq-0.2.1.jar -b -i midi_file.mid
```
output will be `midi_file.mbseq`
```
java -jar mid2mbseq-0.2.1.jar -b -d /home/user/midi_directory
```
output will be `midi_directory.mbseq`

* add `-f` to use full filler option
* add `-m` to use multiple 16 filler option
* add `-c value` to use the filler with custom value
* add `-l` to use set the sequence length

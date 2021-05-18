# assettexttable.ab/skin.txt ==> skin data file(skin.json) for GFToyDroid

outfile = open("skin.json", 'w', encoding='UTF8')
outfile.write("{")

f = open("skin.txt", 'r', encoding='UTF8')
isFistLine = True

lines = f.readlines()
for line in lines:
    if line[5] == '1':
        if not isFistLine:
            outfile.write(",")
        id = int(line[9:13])
        group = 0
        name = line[14:].replace('\n', '').replace('"', '\\"')
        gun = 0
        outfile.write('"%d":{' % id)
        outfile.write('"id":"%d",' % id)
        outfile.write('"group":"%d",' % group)
        outfile.write('"name":"%s",' % name)
        outfile.write('"gun":"%d"' % gun)
        outfile.write("}")
        isFistLine = False
outfile.write("}")

f.close()
outfile.close()
# butterfly-vn

Send butterfly photo & get information back at **Genus** level (ref this [Classification of Living Things](https://github.com/tentamen/butterfly-vn/blob/master/knowledge/dinh_nghia.md) for more info, in Vietnamese).

- Deep Learning retrain via pre-trained Inception v3 model.
- Current **Genus** label available: [output_labels.txt](https://github.com/tentamen/butterfly-vn/blob/master/tf/output_labels.txt)

![messenger](img_2121.jpeg)

## Data stats

|          :genus | :no-imgs | :train? | :vn? |
|-----------------+----------+---------+------|
|         papilio |      122 |       ✅ |    ✅ |
|        graphium |       98 |       ✅ |    ✅ |
|        arhopala |       95 |       ✅ |      |
|         euploea |       65 |       ✅ |    ✅ |
|         junonia |       62 |       ✅ |    ✅ |
|          athyma |       52 |       ✅ |    ✅ |
|       mycalesis |       51 |       ✅ |    ✅ |
|          danaus |       49 |       ✅ |    ✅ |
|        cethosia |       47 |       ✅ |    ✅ |
|          rapala |       47 |       ✅ |    ✅ |
|          eurema |       45 |       ✅ |    ✅ |
|        euthalia |       44 |       ✅ |    ✅ |
|        nacaduba |       44 |       ✅ |      |
|      catopsilia |       38 |       ✅ |    ✅ |
|          appias |       37 |       ✅ |    ✅ |
|        drupadia |       36 |       ✅ |    ✅ |
|          hasora |       34 |       ✅ |    ✅ |
|         polyura |       33 |       ✅ |    ✅ |
|         jamides |       32 |       ✅ |    ✅ |
|          lexias |       32 |       ✅ |    ✅ |
|          delias |       31 |       ✅ |    ✅ |
|        prosotas |       31 |       ✅ |    ✅ |
|       spindasis |       30 |       ✅ |    ✅ |
|        tagiades |       28 |         |    ✅ |
|         tajuria |       28 |         |      |
|          taxila |       28 |         |      |
|          acraea |       26 |         |    ✅ |
|         chilasa |       25 |         |    ✅ |
|       potanthus |       25 |         |      |
|         abisara |       24 |         |      |
|      hypolimnas |       24 |         |    ✅ |
|     hypolycaena |       24 |         |    ✅ |
|        tanaecia |       24 |         |    ✅ |
|       parantica |       23 |         |    ✅ |
|         ypthima |       23 |         |    ✅ |
|        elymnias |       22 |         |    ✅ |
|       pelopidas |       22 |         |      |
|         vindula |       22 |         |    ✅ |
|          caleta |       21 |         |    ✅ |
|         euripus |       21 |         |    ✅ |
|            flos |       21 |         |      |
|           lethe |       21 |         |    ✅ |
|          neptis |       21 |         |    ✅ |
|    catochrysops |       20 |         |      |
|       eulaceura |       20 |         |      |
|            idea |       20 |         |      |
|         anthene |       19 |         |    ✅ |
|         pathysa |       19 |         |    ✅ |
|      cirrochroa |       18 |         |    ✅ |
|          zeltus |       18 |         |    ✅ |
|          laxita |       17 |         |      |
|       melanitis |       17 |         |    ✅ |
|      pachliopta |       17 |         |    ✅ |
|         troides |       17 |         |    ✅ |
|       amathusia |       16 |         |    ✅ |
|  celaenorrhinus |       16 |         |      |
|      plastingia |       16 |         |      |
|          iraota |       15 |         |    ✅ |
|         pratapa |       15 |         |      |
|        telicota |       15 |         |      |
|          zizina |       15 |         |    ✅ |
|       cephrenes |       14 |         |      |
|           cupha |       14 |         |    ✅ |
|          faunis |       14 |         |    ✅ |
|          pieris |       14 |         |      |
|         semanga |       14 |         |      |
|        cheritra |       13 |         |    ✅ |
|         curetis |       13 |         |    ✅ |
|          horaga |       13 |         |      |
|         iambrix |       13 |         |      |
|         lasippa |       13 |         |      |
|         lebadea |       13 |         |    ✅ |
|          loxura |       13 |         |    ✅ |
|        mooreana |       13 |         |    ✅ |
|        phalanta |       13 |         |    ✅ |
|        ideopsis |       12 |         |    ✅ |
|        ionolyce |       12 |         |      |
|    neopithecops |       12 |         |    ✅ |
|         suastus |       12 |         |      |
|         vanessa |       12 |         |      |
|        caltoris |       11 |         |      |
|        chilades |       11 |         |    ✅ |
|        leptosia |       11 |         |    ✅ |
|         poritia |       11 |         |      |
|       pyroneura |       11 |         |      |
|         rachana |       11 |         |      |
|         udaspes |       11 |         |    ✅ |
|        deudorix |       10 |         |      |
|      eooxylides |       10 |         |      |
|        erionota |       10 |         |      |
|        lampides |       10 |         |    ✅ |
|          oriens |       10 |         |      |
|      pantoporia |       10 |         |    ✅ |
| pseudocoladenia |       10 |         |    ✅ |
|         spalgis |       10 |         |      |
|    taractrocera |       10 |         |      |
|         terinos |       10 |         |    ✅ |
|     chersonesia |        9 |         |    ✅ |
|      discophora |        9 |         |    ✅ |
|        hyarotis |        9 |         |      |
|           manto |        9 |         |      |
|          matapa |        9 |         |    ✅ |
|          moduza |        9 |         |    ✅ |
|    odontoptilum |        9 |         |      |
|       pareronia |        9 |         |    ✅ |
|       prioneris |        9 |         |    ✅ |
|       virachola |        9 |         |      |
|          zizula |        9 |         |      |
|         ariadne |        8 |         |    ✅ |
|         badamia |        8 |         |      |
|           borbo |        8 |         |      |
|          burara |        8 |         |      |
|    doleschallia |        8 |         |    ✅ |
|      euchrysops |        8 |         |      |
|     lamproptera |        8 |         |    ✅ |
|         megisba |        8 |         |    ✅ |
|         miletus |        8 |         |      |
|      notocrypta |        8 |         |    ✅ |
|           odina |        8 |         |      |
|     orsotriaena |        8 |         |    ✅ |
|      salanoemia |        8 |         |      |
|        sinthusa |        8 |         |      |
|  stichophthalma |        8 |         |    ✅ |
|        tirumala |        8 |         |    ✅ |
|     zographetus |        8 |         |      |
|   astictopterus |        7 |         |    ✅ |
|          eetion |        7 |         |      |
|         gandaca |        7 |         |      |
|         pandita |        7 |         |      |
|          tapena |        7 |         |      |
|      thaumantis |        7 |         |    ✅ |
|         vagrans |        7 |         |    ✅ |
|        zizeeria |        7 |         |      |
|      acytolepis |        6 |         |      |
|    ancistroides |        6 |         |      |
|       castalius |        6 |         |    ✅ |
|      catopyrops |        6 |         |      |
|          cepora |        6 |         |    ✅ |
|        charaxes |        6 |         |    ✅ |
|        cyrestis |        6 |         |    ✅ |
|          everes |        6 |         |      |
|        hebomoia |        6 |         |    ✅ |
|          hidari |        6 |         |      |
|     neocheritra |        6 |         |      |
|        phaedyma |        6 |         |    ✅ |
|        surendra |        6 |         |      |
|          unkana |        6 |         |      |
|       bindahara |        5 |         |      |
|    catapaecilma |        5 |         |      |
|           halpe |        5 |         |      |
|         kallima |        5 |         |    ✅ |
|         quedara |        5 |         |      |
|            zela |        5 |         |      |
|        zeuxidia |        5 |         |      |
|     amathuxidia |        4 |         |    ✅ |
|      amblipodia |        4 |         |    ✅ |
|        ampittia |        4 |         |      |
|        argyreus |        4 |         |    ✅ |
|         attacus |        4 |         |    ✅ |
|          baoris |        4 |         |      |
|         bibasis |        4 |         |      |
|           byasa |        4 |         |    ✅ |
|        coelites |        4 |         |    ✅ |
|      discolampa |        4 |         |    ✅ |
|         gangara |        4 |         |      |
|     heliophorus |        4 |         |    ✅ |
|         hestina |        4 |         |    ✅ |
|           ixias |        4 |         |    ✅ |
|       neomyrina |        4 |         |    ✅ |
|           neope |        4 |         |    ✅ |
|      paralaxita |        4 |         |    ✅ |
|          pemara |        4 |         |      |
|      polytremis |        4 |         |      |
|        remelana |        4 |         |      |
|     symbrenthia |        4 |         |    ✅ |
|        talicada |        4 |         |    ✅ |
|     teinopalpus |        4 |         |    ✅ |
|         thauria |        4 |         |    ✅ |
|          yasoda |        4 |         |    ✅ |
|       allotinus |        3 |         |      |
|         jacoona |        3 |         |      |
|         logania |        3 |         |      |
|       parthenos |        3 |         |    ✅ |
|       petrelaea |        3 |         |      |
|         ragadia |        3 |         |    ✅ |
|        saletara |        3 |         |      |
|            yoma |        3 |         |    ✅ |
|         liphyra |        2 |         |      |
|      nothodanis |        2 |         |    ✅ |
|   pseudotajuria |        2 |         |      |
|          rohana |        2 |         |    ✅ |
|          aemona |        1 |         |    ✅ |
|          ancema |        1 |         |      |
|          argema |        1 |         |    ✅ |
|    atrophaneura |        1 |         |    ✅ |
|        chliaria |        1 |         |    ✅ |
|         gerosis |        1 |         |      |
|         kaniska |        1 |         |    ✅ |
|         lambrix |        1 |         |    ✅ |
|        libythea |        1 |         |    ✅ |
|  pseudozizeeria |        1 |         |    ✅ |
|           sovia |        1 |         |    ✅ |
|         zemeros |        1 |         |    ✅ |

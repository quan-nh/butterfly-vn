# butterfly-vn

a [Messenger bot](http://m.me/vi.butterfly/) try to predict Butterfly [Species](https://github.com/tentamen/butterfly-vn/blob/master/knowledge/dinh_nghia.md) from photo user submitted.

- Deep Learning retrain via pre-trained Inception v3 model.
- Data stats: [r.txt](r.txt)

![messenger](img_2125.png)

## Development
```sh
$ clj -m www
```

## Deployment
```sh
$ clojure -A:depstar -m hf.depstar.uberjar butterfly.jar
$ java -cp butterfly.jar clojure.main -m www
```

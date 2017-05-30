# st-server

To run the server, you need to install [lein](https://leiningen.org/) first.

After the installation, run the server by typing the following command

```
$ lein figwheel
```

Then open another terminal, input the following command


```
$ lein ring server
```

or

```
$ lein repl

st-server.repl=> (start-server)
```


The steps above will take several minutes to download the dependencies at the first time you run them.

#Sequencer vulnerabilities

##RCE via repl

Clojure developers love to insert REPL in every product, so our service also has a REPL.
REPL is available via parameter 'a' in query - [http://localhost:8080/repl?a=%28str+%27Hello+world%27%29](http://localhost:8080/repl?a=%28str+%27Hello+world%27%29)
But when we try to use it we will get unauthorized because REPL is only available for user with username "ad", 
and when we try to register that user we will get a message that says that username to short to register.

When we look at this part of code:

`(assuming [(empty? (db/get-by-username lower-user))
                "User already exists",
                (< 3 (.length ^String lower-user) 14)
                "Username length must be between 3-14 symbols",
                (= (clojure.string/trim lower-user)
                   (clojure.string/join (re-seq #"[A-Za-z0-9_]" (clojure.string/trim lower-user))))
                "Username should be alphanumeric"]`
We can see that username string is trimmed only in specific functions but not trimmed when checking length.
So we can register a user "ad    " with some spaces or tabs at the end of the string.

Now attack will look like that

`POST "http://localhost:8080/do-register {username:'ad     ', password: 'whatever'}"
GET http://localhost:8080/repl?a=%28apply+str+%28sequencer.db%2Ffind-user-tasks+%27some_username%27%29%29`

##RCE via task string

Another RCE based on clojure dynamic nature. When we look on code of taskparser.clj 
we can notice that the user task generating dynamically from sended file and methods are taking maps insted 
of just strings
`(transform-values-to-string (read-string (get task :data)`
So, read-string func creates a clojure map from string "{:s1 ATGCATGC}", so it can be the place for code execution.
In clojure documentation [https://clojuredocs.org/clojure.core/read-string](https://clojuredocs.org/clojure.core/read-string) 
it says that read-string can execute code with read-eval sequence so if we create a specific data string we can execute any arbittary code.
Atack vector is simple:
`data;{:s1 #=(sequencer.db/find-user-tasks 'username')}` in data file.
No we just send this file and get a flags from checker-user

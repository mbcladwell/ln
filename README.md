# ln

FIXME: description

## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar ln-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2019 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.


(comment
import clojure.java.api.Clojure;
import clojure.lang.IFn;

//setup desired Clojure methods
    private IFn require = Clojure.var("clojure.core", "require");
    require.invoke(Clojure.read("ln.session"));


     IFn getUser = Clojure.var("ln.session", "get-user");
    IFn getPassword = Clojure.var("ln.session", "get-password");
    IFn getURL = Clojure.var("ln.session", "get-url");
    IFn setUser = Clojure.var("ln.session", "set-user");
    IFn setUserID = Clojure.var("ln.session", "set-user-id");
    IFn getUserID = Clojure.var("ln.session", "get-user-id");
    IFn setUserGroup = Clojure.var("ln.session", "set-user-group");
    IFn getUserGroup = Clojure.var("ln.session", "get-user-group");
    IFn setAuthenticated = Clojure.var("ln.session", "set-authenticated");
    IFn setSessionID = Clojure.var("ln.session", "set-session-id");
    IFn getSessionID = Clojure.var("ln.session", "get-session-id");
    IFn setPlateID = Clojure.var("ln.session", "set-plate-id");
    IFn setPlateSetID = Clojure.var("ln.session", "set-plate-set-id");
    IFn setPlateSetSysName = Clojure.var("ln.session", "set-plate-set-sys-name");
    IFn getHelpURLPrefix = Clojure.var("ln.session", "get-help-url-prefix");
    IFn getProjectSysName = Clojure.var("ln.session", "get-project-sys-name");
    IFn setProjectSysName = Clojure.var("ln.session", "set-project-sys-name");
    IFn getProjectID = Clojure.var("ln.session", "get-project-id");
   IFn setProjectID = Clojure.var("ln.session", "set-project-id");

    require.invoke(Clojure.read("ln.db"));
    IFn dropAllTables = Clojure.var("ln.db", "drop-all-tables");
    IFn initLimsNucleus = Clojure.var("ln.db", "initialize-limsnucleus");
)

M-x set-buffer-file-coding-system utf-8-unix


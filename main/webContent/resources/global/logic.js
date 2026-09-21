window.PizzaProcess = window.PizzaProcess || {};

(function (app) {
  'use strict';

  app.modules = app.modules || {};

  app.register = function (name, module) {
    app.modules[name] = module;
    if (typeof module.init === 'function') {
      module.init();
    }
  };

  app.initAll = function () {
    Object.keys(app.modules).forEach(function (name) {
      var module = app.modules[name];
      if (module && typeof module.init === 'function') {
        module.init();
      }
    });
  };

  /**
   * primefaces fires this on every completed ajax request.
   * TODO: if i only want to re-init when a specific area was updated, inspect args.responseXML for the updated client ids
   */
  if (window.$) {
    $(document).on('pfAjaxComplete', function () {
      app.initAll();
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    app.initAll();
  });

})(window.PizzaProcess);
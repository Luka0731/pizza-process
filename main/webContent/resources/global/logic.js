window.PizzaProcess = window.PizzaProcess || {};

/**
 * Tiny module system for the client side logic of the pizza process.
 *
 * A module registers itself once:
 *
 *   PizzaProcess.register('pizzaCards', {
 *     init: function (roots) {
 *       roots.forEach(function (root) {
 *         root.querySelectorAll('.pizza-card').forEach(...);
 *       });
 *     }
 *   });
 *
 * init(roots) is called on page load with [document], and after every ajax request with only the elements
 * that primefaces just replaced. A module only searches inside the roots, so nothing gets initialised twice
 * and parts of the page that were not updated are left alone.
 */
(function (app) {
  'use strict';

  app.modules = app.modules || {};

  app.register = function (name, module) {
    app.modules[name] = module;
    if (document.readyState !== 'loading') {
      initModule(module, [document]);
    }
  };

  app.initAll = function (roots) {
    Object.keys(app.modules).forEach(function (name) {
      initModule(app.modules[name], roots || [document]);
    });
  };



  // |----- helpers -----|

  function initModule(module, roots) {
    if (module && typeof module.init === 'function' && roots.length > 0) {
      module.init(roots);
    }
  }

  /**
   * reads the ids of the updated areas out of the jsf partial response:
   * <partial-response><changes><update id="form:pageContent">...</update></changes></partial-response>
   */
  function updatedRoots(xhr) {
    var responseXML = xhr && xhr.responseXML;
    if (!responseXML) return [document];

    var roots = [];
    var updates = responseXML.getElementsByTagName('update');
    for (var i = 0; i < updates.length; i++) {
      var element = document.getElementById(updates[i].getAttribute('id'));
      if (element) roots.push(element);
    }
    return roots;
  }



  // |----- lifecycle -----|

  // primefaces fires this after every ajax request and after the dom was updated
  if (window.$) {
    $(document).on('pfAjaxComplete', function (event, xhr) {
      app.initAll(updatedRoots(xhr));
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    app.initAll([document]);
  });

})(window.PizzaProcess);

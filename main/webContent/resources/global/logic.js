/**
 * TODO: add description
**/
class PizzaProcess {
  static #modules = {};



  static register(name, module) {
    PizzaProcess.#modules[name] = module;
    if (document.readyState !== 'loading') {
      PizzaProcess.#init(module, [document]);
    }
  }

  static initAll(roots = [document]) {
    Object.values(PizzaProcess.#modules).forEach((module) => {
      PizzaProcess.#init(module, roots);
    });
  }



  // |----- helper methods -----|

  static #init(module, roots) {
    if (module && typeof module.init === 'function' && roots.length > 0) {
      module.init(roots);
    }
  }
  static updatedRoots(xhr) {
    const responseXML = xhr && xhr.responseXML;
    if (!responseXML) return [document];

    const roots = [];
    for (const update of responseXML.getElementsByTagName('update')) {
      const element = document.getElementById(update.getAttribute('id'));
      if (element) roots.push(element);
    }
    return roots;
  }
}





// |----- lifecycle -----|

window.PizzaProcess = PizzaProcess;

document.addEventListener('DOMContentLoaded', () => {
  PizzaProcess.initAll([document]);
});

if (window.$) {
  $(document).on('pfAjaxComplete', (event, xhr) => {
    PizzaProcess.initAll(PizzaProcess.updatedRoots(xhr));
  });
}
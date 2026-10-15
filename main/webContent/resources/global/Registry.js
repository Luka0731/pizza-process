class Registry {
  static #modules = {};



  static register(name, module) {
    Registry.#modules[name] = module;
    if (document.readyState !== 'loading') {
      Registry.#init(module, [document]);
    }
  }

  static initAll(roots = [document]) {
    Object.values(Registry.#modules).forEach((module) => {
      Registry.#init(module, roots);
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

window.Registry = Registry;

document.addEventListener('DOMContentLoaded', () => {
  Registry.initAll([document]);
});

if (window.$) {
  $(document).on('pfAjaxComplete', (event, xhr) => {
    Registry.initAll(Registry.updatedRoots(xhr));
  });
}
class EatThePizzaDialogPage {

  static init(roots) {
    for (const root of roots) {
      for (const table of root.querySelectorAll('.eat-page__table:not([data-ready])')) {
        table.dataset.ready = 'true';
        EatThePizzaDialogPage.#setUp(table);
      }
    }
  }



  // |----- game -----|

  static #setUp(table) {
    for (const pizza of table.querySelectorAll('.eat-page__pizza')) {
      EatThePizzaDialogPage.#place(pizza, table);
      pizza.addEventListener('click', () => EatThePizzaDialogPage.#eat(pizza, table), { once: true });
    }
  }

  static #place(pizza, table) {
    const size = Math.min(table.clientWidth, table.clientHeight) * (0.22 + Math.random() * 0.12);
    Object.assign(pizza.style, {
      width: `${size}px`,
      height: `${size}px`,
      left: `${Math.random() * (table.clientWidth - size)}px`,
      top: `${Math.random() * (table.clientHeight - size)}px`,
      transform: `rotate(${Math.random() * 60 - 30}deg)`,
    });
  }

  static #eat(pizza, table) {
    pizza.classList.add('eat-page__pizza--eaten');
      new Audio(table.dataset.eatSound).play().catch(() => {});

    const pizzasLeft = table.querySelectorAll('.eat-page__pizza:not(.eat-page__pizza--eaten)').length;
    if (pizzasLeft === 0) {
      setTimeout(() => EatThePizzaDialogPage.#showThanks(table), 600);
    }
  }

  static #showThanks(table) {
    table.parentNode.querySelector('.eat-page__thanks').classList.add('eat-page__thanks--visible');
  }
}





document.addEventListener('DOMContentLoaded', () => {
  PizzaProcess.register('EatThePizzaDialogPage', EatThePizzaDialogPage);
});
/**
 * The little game at the end: every ordered pizza lies somewhere on the table, a click eats it.
 * When the last one is gone, the thanks card shows up.
**/
class EatThePizzaDialogPage {
  static #crumbColors = ['--terracotta', '--ochre', '--clay', '--olive', '--bone'];

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
    EatThePizzaDialogPage.#crumbs(pizza, table);
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



  // |----- effects -----|

  // a handful of square crumbs fly out of the pizza and fall down a bit
  static #crumbs(pizza, table) {
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches || !Element.prototype.animate) return;

    const centerX = pizza.offsetLeft + pizza.offsetWidth / 2;
    const centerY = pizza.offsetTop + pizza.offsetHeight / 2;
    const styles = getComputedStyle(document.documentElement);

    for (let i = 0; i < 12; i++) {
      const crumb = document.createElement('span');
      const color = EatThePizzaDialogPage.#crumbColors[i % EatThePizzaDialogPage.#crumbColors.length];
      crumb.className = 'eat-page__crumb';
      crumb.style.left = `${centerX}px`;
      crumb.style.top = `${centerY}px`;
      crumb.style.background = styles.getPropertyValue(color);
      table.appendChild(crumb);

      const angle = Math.random() * Math.PI * 2;
      const distance = pizza.offsetWidth * (0.4 + Math.random() * 0.5);
      const x = Math.cos(angle) * distance;
      const y = Math.sin(angle) * distance;
      crumb
        .animate(
          [
            { transform: 'translate(0, 0) rotate(0deg)', opacity: 1 },
            { transform: `translate(${x}px, ${y}px) rotate(${Math.random() * 360}deg)`, opacity: 1, offset: 0.6 },
            { transform: `translate(${x}px, ${y + 40}px) rotate(${Math.random() * 360}deg)`, opacity: 0 },
          ],
          { duration: 520, easing: 'steps(10, end)' },
        )
        .finished.then(() => crumb.remove());
    }
  }
}





// |----- lifecycle -----|

PizzaProcess.register('EatThePizzaDialogPage', EatThePizzaDialogPage);

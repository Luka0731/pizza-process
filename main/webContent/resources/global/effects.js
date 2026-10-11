/**
 * Visual effects for every dialog. Everything in here is decoration: if it breaks, the pages still work.
 *
 * - page change:  the entrance animations (style.css) run whenever a page lands in the dom.
 *                 when the same page only reloads (poll, validation error, removing a cart item) they are switched off.
 * - loading bar:  thin terracotta bar on top while an ajax request runs. requests with global="false" do not show it.
 * - cart badge:   jumps when the number in it goes up.
 * - scramble:     text with the class "fx-scramble" rolls through random digits before it settles, e.g. the price.
 *
 * Registered at the bottom with PizzaProcess.register(), see logic.js.
**/
class Effects {
  static #pageId = null;
  static #cartCount = null;
  static #bar = null;
  static #reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  static init(roots) {
    Effects.#mountLoadingBar();
    for (const root of roots) {
      Effects.#quietSamePage(root);
      Effects.#bumpCart(root);
      Effects.#scramble(root);
    }
  }



  // |----- page change -----|

  static #quietSamePage(root) {
    const container = root === document ? document.querySelector('.dialog-content') : root;
    if (!container || !container.classList || !container.classList.contains('dialog-content')) return;

    const page = container.querySelector(':scope > div[id]');
    if (!page) return;

    if (page.id === Effects.#pageId) page.classList.add('fx-quiet');
    Effects.#pageId = page.id;
  }



  // |----- loading bar -----|

  static #mountLoadingBar() {
    if (Effects.#bar || !window.$) return;

    Effects.#bar = document.createElement('div');
    Effects.#bar.className = 'fx-loading-bar';
    document.body.appendChild(Effects.#bar);

    $(document).on('pfAjaxSend', () => {
      const bar = Effects.#bar;
      bar.classList.remove('fx-loading-bar--done', 'fx-loading-bar--running');
      void bar.offsetWidth; // restart from 0
      bar.classList.add('fx-loading-bar--running');
    });
    $(document).on('pfAjaxComplete', () => {
      const bar = Effects.#bar;
      bar.classList.add('fx-loading-bar--done');
      setTimeout(() => bar.classList.remove('fx-loading-bar--running', 'fx-loading-bar--done'), 400);
    });
  }



  // |----- cart badge -----|

  static #bumpCart(root) {
    const count = (root === document ? document : root.ownerDocument).querySelector('.cart-badge__count');
    if (!count) return;

    const value = Number(count.dataset.count);
    if (Effects.#cartCount !== null && value > Effects.#cartCount && !Effects.#reducedMotion) {
      count.closest('.cart-badge').classList.add('cart-badge--bumped');
    }
    Effects.#cartCount = value;
  }



  // |----- scramble -----|

  static #scramble(root) {
    const targets = [...root.querySelectorAll('.fx-scramble')];
    if (root.classList && root.classList.contains('fx-scramble')) targets.push(root);

    for (const element of targets) {
      if (element.dataset.scrambled || Effects.#reducedMotion) continue;
      element.dataset.scrambled = 'true';
      Effects.#rollDigits(element);
    }
  }

  static #rollDigits(element) {
    const finalText = element.textContent;
    const digitPositions = [...finalText].map((char, i) => (/\d/.test(char) ? i : -1)).filter((i) => i >= 0);
    const frames = 7;
    let frame = 0;

    const timer = setInterval(() => {
      frame++;
      // digits lock in from left to right, the rest keep rolling
      const locked = Math.floor((digitPositions.length * frame) / frames);
      const chars = [...finalText];
      digitPositions.slice(locked).forEach((i) => (chars[i] = String(Math.floor(Math.random() * 10))));
      element.textContent = chars.join('');

      if (frame >= frames) {
        clearInterval(timer);
        element.textContent = finalText;
      }
    }, 40);
  }
}





// |----- lifecycle -----|

PizzaProcess.register('Effects', Effects);

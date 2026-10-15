class Effects {
  static LOADING_DELAY = 300;
  static #pageId = null;
  static #cartCount = null;
  static #bar = null;
  static #barTimer = null;
  static #enterReady = false;
  static #reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  static init(roots) {
    Effects.#mountLoadingBar();
    Effects.#mountEnterSubmit();
    for (const root of roots) {
      Effects.#markEntrance(root);
      Effects.#bumpCart(root);
      Effects.#scramble(root);
      Effects.#trimFieldErrors(root);
    }
  }



  // |----- page change -----|

  static #markEntrance(root) {
    if (Effects.#reducedMotion) return;

    const container = root === document ? document.querySelector('.dialog-content') : root;
    if (container && container.classList && container.classList.contains('dialog-content')) {
      const page = container.querySelector(':scope > div[id]');
      if (page && page.id !== Effects.#pageId) page.classList.add('fx-enter');
      if (page) Effects.#pageId = page.id;
      return;
    }

    // a part of a page that shows a list, e.g. the menu after a search
    if (root !== document && root.querySelector && root.querySelector('.pizza-card, .pizza-row, .item-card')) {
      root.classList.add('fx-enter');
    }
  }



  // |----- loading bar -----|

  static #mountLoadingBar() {
    if (Effects.#bar || !window.$) return;

    Effects.#bar = document.createElement('div');
    Effects.#bar.className = 'fx-loading-bar';
    document.body.appendChild(Effects.#bar);

    $(document).on('pfAjaxSend', () => {
      clearTimeout(Effects.#barTimer);
      Effects.#barTimer = setTimeout(() => {
        const bar = Effects.#bar;
        bar.classList.remove('fx-loading-bar--done', 'fx-loading-bar--running');
        void bar.offsetWidth; // restart from 0
        bar.classList.add('fx-loading-bar--running');
      }, Effects.LOADING_DELAY);
    });
    $(document).on('pfAjaxComplete', () => {
      clearTimeout(Effects.#barTimer);
      const bar = Effects.#bar;
      if (!bar.classList.contains('fx-loading-bar--running')) return;
      bar.classList.add('fx-loading-bar--done');
      setTimeout(() => bar.classList.remove('fx-loading-bar--running', 'fx-loading-bar--done'), 400);
    });
  }



  // |----- cart button -----|

  static #bumpCart(root) {
    const count = (root === document ? document : root.ownerDocument).querySelector('.cart-button__count');
    if (!count) return;

    const value = Number(count.dataset.count);
    if (Effects.#cartCount !== null && value > Effects.#cartCount && !Effects.#reducedMotion) {
      count.closest('.cart-button').classList.add('cart-button--bumped');
    }
    Effects.#cartCount = value;
  }



  // |----- enter -----|

  static #mountEnterSubmit() {
    if (Effects.#enterReady) return;
    Effects.#enterReady = true;

    document.addEventListener('keydown', (event) => {
      if (event.key !== 'Enter' || event.target.tagName !== 'INPUT') return;
      const card = event.target.closest('.card');
      const button = card && card.querySelector('.form-submit:not([disabled]):not(.ui-state-disabled)');
      if (!button) return;

      event.preventDefault();
      event.stopImmediatePropagation();
      button.click();
    }, true);
  }



  // |----- field errors -----|

  static #trimFieldErrors(root) {
    const scope = root === document ? document : root;
    const fields = scope.classList && scope.classList.contains('text-input-field') ? [scope] : scope.querySelectorAll('.text-input-field');

    for (const field of fields) {
      const label = field.querySelector('.text-input-field__label');
      const message = field.querySelector('.text-input-field__error .ui-message-error-detail');
      if (!label || !message) continue;

      const prefix = label.textContent.replace('*', '').trim() + ':';
      const text = message.textContent.trim();
      if (text.toLowerCase().startsWith(prefix.toLowerCase())) {
        message.textContent = text.slice(prefix.length).trim();
      }
    }
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





Registry.register('Effects', Effects);
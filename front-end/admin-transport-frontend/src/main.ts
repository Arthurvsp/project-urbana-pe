import 'zone.js';  // ← Adicione esta linha aqui!

import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app';  // ou o path correto para AppComponent

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
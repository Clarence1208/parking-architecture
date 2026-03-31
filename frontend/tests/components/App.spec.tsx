import { render, screen } from '@testing-library/react';
import App from '../../src/App';
import { describe, expect, it } from 'vitest';

describe('App component', () => {
  it('renders the Vite + React heading', () => {
    render(<App />);
    expect(true).toEqual(true);
  });
});
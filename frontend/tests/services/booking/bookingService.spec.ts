import { describe, it, expect, vi, beforeEach } from 'vitest';
import {bookingService} from "../../../src/services/booking/bookingService";
import {apiClient} from "../../../src/services/api/apiClient";

vi.mock('../../../src/services/api/apiClient', () => ({
    apiClient: {
        get: vi.fn(() => Promise.resolve([])),
        post: vi.fn(() => Promise.resolve({})),
        delete: vi.fn(() => Promise.resolve({})),
    },
}));

describe('BookingService Specification', () => {

    beforeEach(() => {
        vi.clearAllMocks(); // On nettoie les compteurs entre chaque test
    });

    describe('getParkingStatus()', () => {
        it('should call the correct URL without date', async () => {
            await bookingService.getParkingStatus();
            expect(apiClient.get).toHaveBeenCalledWith('/booking/spots');
        });

        it('should append date as a query parameter when provided', async () => {
            const testDate = '2026-05-20';
            await bookingService.getParkingStatus(testDate);
            expect(apiClient.get).toHaveBeenCalledWith(`/booking/spots?date=${testDate}`);
        });
    });

    describe('createReservation()', () => {
        it('should post the correct reservation data', async () => {
            const mockReservation = {
                spotId: 'A01',
                startDate: '2026-04-10',
                endDate: '2026-04-12'
            };

            await bookingService.createReservation(mockReservation);

            expect(apiClient.post).toHaveBeenCalledWith('/booking/reserve', mockReservation);
        });
    });

    describe('cancelReservation()', () => {
        it('should call delete with the correct bookingId', async () => {
            const bookingId = 123;
            await bookingService.cancelReservation(bookingId);

            expect(apiClient.delete).toHaveBeenCalledWith(`/booking/${bookingId}`);
        });
    });

    describe('checkIn()', () => {
        it('should send spotId in the request body', async () => {
            const spotId = 'B05';
            await bookingService.checkIn(spotId);

            expect(apiClient.post).toHaveBeenCalledWith('/booking/check-in', { spotId });
        });
    });
});